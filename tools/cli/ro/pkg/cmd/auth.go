package cmd

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net"
	"net/http"
	"net/url"
	"os"
	"strings"
	"time"

	"github.com/spf13/cobra"
)

const (
	authDefaultServer   = "http://localhost:8081"
	authDefaultRealm    = "rallyon"
	authDefaultClientID = "rallyon-api"
	authDefaultUsername = "dev.organizer"
	authDefaultFormat   = "raw"
)

var (
	flagAuthServer       string
	flagAuthRealm        string
	flagAuthClientID     string
	flagAuthClientSecret string
	flagAuthUsername     string
	flagAuthPassword     string
	flagAuthFormat       string

	authHTTPClient = &http.Client{Timeout: 10 * time.Second}
)

type authTokenRequest struct {
	Server       string
	Realm        string
	ClientID     string
	ClientSecret string
	Username     string
	Password     string
	Format       string
}

type keycloakTokenResponse struct {
	AccessToken string `json:"access_token"`
	TokenType   string `json:"token_type"`
	ExpiresIn   int    `json:"expires_in"`
	Scope       string `json:"scope,omitempty"`
}

type keycloakErrorResponse struct {
	Error            string `json:"error"`
	ErrorDescription string `json:"error_description"`
}

var authCmd = &cobra.Command{
	Use:   "auth",
	Short: "Local development authentication helpers",
	Long:  "Local development authentication helpers for RallyOn workflows such as Swagger UI testing.",
}

var authTokenCmd = &cobra.Command{
	Use:           "token",
	Short:         "Request a local Keycloak bearer token for Swagger UI",
	SilenceUsage:  true,
	SilenceErrors: true,
	Long: strings.Join([]string{
		"Request a local development access token from the RallyOn Keycloak realm.",
		"This command is intended for local Swagger UI and manual API testing only.",
	}, " "),
	Example: strings.Join([]string{
		"RALLYON_CLIENT_SECRET=super-secret ro auth token --format bearer",
		"RALLYON_CLIENT_SECRET=super-secret ro auth token | tr -d '\\n'",
	}, "\n"),
	RunE: func(cmd *cobra.Command, args []string) error {
		request, err := resolveAuthTokenRequest()
		if err != nil {
			return err
		}

		response, err := fetchKeycloakToken(cmd.Context(), authHTTPClient, request)
		if err != nil {
			return err
		}

		return writeAuthTokenOutput(cmd.OutOrStdout(), request.Format, response)
	},
}

func init() {
	authTokenCmd.Flags().StringVar(&flagAuthServer, "server", "", "Keycloak server base URL (default: KEYCLOAK_SERVER or http://localhost:8081)")
	authTokenCmd.Flags().StringVar(&flagAuthRealm, "realm", "", "realm name (default: RALLYON_REALM or rallyon)")
	authTokenCmd.Flags().StringVar(&flagAuthClientID, "client-id", "", "client ID (default: RALLYON_CLIENT_ID or rallyon-api)")
	authTokenCmd.Flags().StringVar(&flagAuthClientSecret, "client-secret", "", "client secret (default: RALLYON_CLIENT_SECRET)")
	authTokenCmd.Flags().StringVar(&flagAuthUsername, "username", "", "username (default: RALLYON_DEV_USERNAME or dev.organizer)")
	authTokenCmd.Flags().StringVar(&flagAuthPassword, "password", "", "password (default: RALLYON_DEV_PASSWORD)")
	authTokenCmd.Flags().StringVar(&flagAuthFormat, "format", authDefaultFormat, "output format: raw, bearer, or json")

	authCmd.AddCommand(authTokenCmd)
	rootCmd.AddCommand(authCmd)
}

func resolveAuthTokenRequest() (authTokenRequest, error) {
	format := strings.ToLower(strings.TrimSpace(firstNonEmpty(flagAuthFormat, authDefaultFormat)))
	if format != "raw" && format != "bearer" && format != "json" {
		return authTokenRequest{}, fmt.Errorf("unsupported format %q (expected raw, bearer, or json)", format)
	}

	request := authTokenRequest{
		Server:       firstNonEmpty(flagAuthServer, os.Getenv("KEYCLOAK_SERVER"), authDefaultServer),
		Realm:        firstNonEmpty(flagAuthRealm, os.Getenv("RALLYON_REALM"), authDefaultRealm),
		ClientID:     firstNonEmpty(flagAuthClientID, os.Getenv("RALLYON_CLIENT_ID"), authDefaultClientID),
		ClientSecret: firstNonEmpty(flagAuthClientSecret, os.Getenv("RALLYON_CLIENT_SECRET")),
		Username:     firstNonEmpty(flagAuthUsername, os.Getenv("RALLYON_DEV_USERNAME"), authDefaultUsername),
		Password:     firstNonEmpty(flagAuthPassword, os.Getenv("RALLYON_DEV_PASSWORD")),
		Format:       format,
	}

	if strings.TrimSpace(request.ClientSecret) == "" {
		return authTokenRequest{}, fmt.Errorf(
			"missing client secret: set RALLYON_CLIENT_SECRET or pass --client-secret\n%s",
			authSetupHint(),
		)
	}

	if strings.TrimSpace(request.Password) == "" {
		return authTokenRequest{}, fmt.Errorf(
			"missing developer password: set RALLYON_DEV_PASSWORD or pass --password\n%s",
			authSetupHint(),
		)
	}

	return request, nil
}

func fetchKeycloakToken(ctx context.Context, client *http.Client, request authTokenRequest) (*keycloakTokenResponse, error) {
	if client == nil {
		client = authHTTPClient
	}
	if ctx == nil {
		ctx = context.Background()
	}

	tokenURL := strings.TrimRight(request.Server, "/") + "/realms/" + strings.Trim(request.Realm, "/") + "/protocol/openid-connect/token"
	form := url.Values{
		"grant_type":    {"password"},
		"client_id":     {request.ClientID},
		"client_secret": {request.ClientSecret},
		"username":      {request.Username},
		"password":      {request.Password},
	}

	req, err := http.NewRequestWithContext(ctx, http.MethodPost, tokenURL, strings.NewReader(form.Encode()))
	if err != nil {
		return nil, fmt.Errorf("build token request: %w", err)
	}
	req.Header.Set("Content-Type", "application/x-www-form-urlencoded")

	resp, err := client.Do(req)
	if err != nil {
		return nil, formatAuthTransportError(tokenURL, err)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(io.LimitReader(resp.Body, 16*1024))
	if err != nil {
		return nil, fmt.Errorf("read token response: %w", err)
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		var authErr keycloakErrorResponse
		if json.Unmarshal(body, &authErr) == nil && (authErr.Error != "" || authErr.ErrorDescription != "") {
			description := strings.TrimSpace(authErr.ErrorDescription)
			if description == "" {
				description = strings.TrimSpace(authErr.Error)
			}
			return nil, fmt.Errorf(
				"token request failed (%s): %s\n%s",
				resp.Status,
				description,
				authSetupHint(),
			)
		}

		message := strings.TrimSpace(string(body))
		if message == "" {
			message = "empty response body"
		}
		return nil, fmt.Errorf(
			"token request failed (%s): %s\n%s",
			resp.Status,
			message,
			authSetupHint(),
		)
	}

	var tokenResponse keycloakTokenResponse
	if err := json.Unmarshal(body, &tokenResponse); err != nil {
		return nil, fmt.Errorf("decode token response: %w", err)
	}
	if strings.TrimSpace(tokenResponse.AccessToken) == "" {
		return nil, fmt.Errorf("token response did not include an access_token")
	}

	return &tokenResponse, nil
}

func writeAuthTokenOutput(w io.Writer, format string, response *keycloakTokenResponse) error {
	if response == nil {
		return errors.New("token response is required")
	}

	switch format {
	case "raw":
		_, err := fmt.Fprintln(w, response.AccessToken)
		return err
	case "bearer":
		_, err := fmt.Fprintf(w, "Bearer %s\n", response.AccessToken)
		return err
	case "json":
		enc := json.NewEncoder(w)
		enc.SetIndent("", "  ")
		return enc.Encode(response)
	default:
		return fmt.Errorf("unsupported format %q", format)
	}
}

func formatAuthTransportError(tokenURL string, err error) error {
	var netErr net.Error
	switch {
	case errors.As(err, &netErr) && netErr.Timeout():
		return fmt.Errorf("token request timed out for %s\n%s", tokenURL, authSetupHint())
	case errors.Is(err, context.DeadlineExceeded):
		return fmt.Errorf("token request timed out for %s\n%s", tokenURL, authSetupHint())
	default:
		return fmt.Errorf("token request could not reach %s: %w\n%s", tokenURL, err, authSetupHint())
	}
}

func authSetupHint() string {
	return strings.Join([]string{
		"To prepare local auth, run:",
		"  docker compose -f infrastructure/local/docker-compose.yml up -d",
		"  export RALLYON_CLIENT_SECRET=...",
		"  bash admin/keycloak/provision_keycloak.sh",
	}, "\n")
}

func firstNonEmpty(values ...string) string {
	for _, value := range values {
		trimmed := strings.TrimSpace(value)
		if trimmed != "" {
			return trimmed
		}
	}
	return ""
}
