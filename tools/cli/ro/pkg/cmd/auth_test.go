package cmd

import (
	"bytes"
	"context"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"
)

func TestResolveAuthTokenRequest(t *testing.T) {
	origServer := flagAuthServer
	origRealm := flagAuthRealm
	origClientID := flagAuthClientID
	origClientSecret := flagAuthClientSecret
	origUsername := flagAuthUsername
	origPassword := flagAuthPassword
	origFormat := flagAuthFormat

	t.Cleanup(func() {
		flagAuthServer = origServer
		flagAuthRealm = origRealm
		flagAuthClientID = origClientID
		flagAuthClientSecret = origClientSecret
		flagAuthUsername = origUsername
		flagAuthPassword = origPassword
		flagAuthFormat = origFormat
		t.Setenv("KEYCLOAK_SERVER", "")
		t.Setenv("RALLYON_REALM", "")
		t.Setenv("RALLYON_CLIENT_ID", "")
		t.Setenv("RALLYON_CLIENT_SECRET", "")
		t.Setenv("RALLYON_DEV_USERNAME", "")
		t.Setenv("RALLYON_DEV_PASSWORD", "")
	})

	flagAuthServer = ""
	flagAuthRealm = ""
	flagAuthClientID = ""
	flagAuthClientSecret = ""
	flagAuthUsername = ""
	flagAuthPassword = ""
	flagAuthFormat = ""

	t.Setenv("RALLYON_CLIENT_SECRET", "super-secret")
	t.Setenv("RALLYON_DEV_PASSWORD", "DevOrganizer!1")

	request, err := resolveAuthTokenRequest()
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if request.Server != authDefaultServer {
		t.Fatalf("unexpected default server: %s", request.Server)
	}
	if request.Realm != authDefaultRealm {
		t.Fatalf("unexpected default realm: %s", request.Realm)
	}
	if request.ClientID != authDefaultClientID {
		t.Fatalf("unexpected default client id: %s", request.ClientID)
	}
	if request.Username != authDefaultUsername {
		t.Fatalf("unexpected default username: %s", request.Username)
	}
	if request.Format != authDefaultFormat {
		t.Fatalf("unexpected default format: %s", request.Format)
	}
}

func TestResolveAuthTokenRequestRequiresClientSecret(t *testing.T) {
	origClientSecret := flagAuthClientSecret
	origPassword := flagAuthPassword
	origFormat := flagAuthFormat
	t.Cleanup(func() {
		flagAuthClientSecret = origClientSecret
		flagAuthPassword = origPassword
		flagAuthFormat = origFormat
	})

	flagAuthClientSecret = ""
	flagAuthPassword = "DevOrganizer!1"
	flagAuthFormat = "raw"

	request, err := resolveAuthTokenRequest()
	if err == nil {
		t.Fatalf("expected error, got request %#v", request)
	}
	if !strings.Contains(err.Error(), "missing client secret") {
		t.Fatalf("unexpected error: %v", err)
	}
}

func TestFetchKeycloakToken(t *testing.T) {
	t.Run("returns token on success", func(t *testing.T) {
		srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			if r.URL.Path != "/realms/rallyon/protocol/openid-connect/token" {
				t.Fatalf("unexpected path: %s", r.URL.Path)
			}
			if err := r.ParseForm(); err != nil {
				t.Fatalf("parse form: %v", err)
			}
			if got := r.FormValue("client_id"); got != "rallyon-api" {
				t.Fatalf("unexpected client_id: %s", got)
			}
			if got := r.FormValue("grant_type"); got != "password" {
				t.Fatalf("unexpected grant_type: %s", got)
			}
			w.Header().Set("Content-Type", "application/json")
			_, _ = w.Write([]byte(`{"access_token":"abc123","token_type":"Bearer","expires_in":300}`))
		}))
		defer srv.Close()

		response, err := fetchKeycloakToken(context.Background(), srv.Client(), authTokenRequest{
			Server:       srv.URL,
			Realm:        "rallyon",
			ClientID:     "rallyon-api",
			ClientSecret: "super-secret",
			Username:     "dev.organizer",
			Password:     "DevOrganizer!1",
		})
		if err != nil {
			t.Fatalf("unexpected error: %v", err)
		}
		if response.AccessToken != "abc123" {
			t.Fatalf("unexpected token: %s", response.AccessToken)
		}
	})

	t.Run("returns helpful error on unauthorized", func(t *testing.T) {
		srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			w.WriteHeader(http.StatusUnauthorized)
			_, _ = w.Write([]byte(`{"error":"invalid_grant","error_description":"Bad credentials"}`))
		}))
		defer srv.Close()

		_, err := fetchKeycloakToken(context.Background(), srv.Client(), authTokenRequest{
			Server:       srv.URL,
			Realm:        "rallyon",
			ClientID:     "rallyon-api",
			ClientSecret: "super-secret",
			Username:     "dev.organizer",
			Password:     "wrong",
		})
		if err == nil {
			t.Fatalf("expected unauthorized error")
		}
		if !strings.Contains(err.Error(), "Bad credentials") {
			t.Fatalf("unexpected error: %v", err)
		}
	})

	t.Run("returns error on malformed json", func(t *testing.T) {
		srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			w.Header().Set("Content-Type", "application/json")
			_, _ = w.Write([]byte(`{"access_token":`))
		}))
		defer srv.Close()

		_, err := fetchKeycloakToken(context.Background(), srv.Client(), authTokenRequest{
			Server:       srv.URL,
			Realm:        "rallyon",
			ClientID:     "rallyon-api",
			ClientSecret: "super-secret",
			Username:     "dev.organizer",
			Password:     "DevOrganizer!1",
		})
		if err == nil {
			t.Fatalf("expected json decode error")
		}
		if !strings.Contains(err.Error(), "decode token response") {
			t.Fatalf("unexpected error: %v", err)
		}
	})

	t.Run("returns helpful error when server is unreachable", func(t *testing.T) {
		client := &http.Client{Timeout: 20 * time.Millisecond}
		_, err := fetchKeycloakToken(context.Background(), client, authTokenRequest{
			Server:       "http://127.0.0.1:1",
			Realm:        "rallyon",
			ClientID:     "rallyon-api",
			ClientSecret: "super-secret",
			Username:     "dev.organizer",
			Password:     "DevOrganizer!1",
		})
		if err == nil {
			t.Fatalf("expected connection error")
		}
		if !strings.Contains(err.Error(), "could not reach") {
			t.Fatalf("unexpected error: %v", err)
		}
	})
}

func TestWriteAuthTokenOutput(t *testing.T) {
	response := &keycloakTokenResponse{
		AccessToken: "abc123",
		TokenType:   "Bearer",
		ExpiresIn:   300,
		Scope:       "openid profile",
	}

	t.Run("raw prints only the token", func(t *testing.T) {
		var buf bytes.Buffer
		if err := writeAuthTokenOutput(&buf, "raw", response); err != nil {
			t.Fatalf("unexpected error: %v", err)
		}
		if got := buf.String(); got != "abc123\n" {
			t.Fatalf("unexpected raw output: %q", got)
		}
	})

	t.Run("bearer prints prefixed token", func(t *testing.T) {
		var buf bytes.Buffer
		if err := writeAuthTokenOutput(&buf, "bearer", response); err != nil {
			t.Fatalf("unexpected error: %v", err)
		}
		if got := buf.String(); got != "Bearer abc123\n" {
			t.Fatalf("unexpected bearer output: %q", got)
		}
	})

	t.Run("json prints token metadata", func(t *testing.T) {
		var buf bytes.Buffer
		if err := writeAuthTokenOutput(&buf, "json", response); err != nil {
			t.Fatalf("unexpected error: %v", err)
		}
		output := buf.String()
		if !strings.Contains(output, `"access_token": "abc123"`) {
			t.Fatalf("unexpected json output: %q", output)
		}
		if !strings.Contains(output, `"expires_in": 300`) {
			t.Fatalf("unexpected json output: %q", output)
		}
	})
}
