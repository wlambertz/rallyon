package cmd

import (
	"context"
	"fmt"
	"log/slog"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
)

var (
	flagRunPort int
	flagRunEnv  string
)

var runCmd = &cobra.Command{
	Use:   "run",
	Short: "Run local services",
}

var runTournamentmgmtCmd = &cobra.Command{
	Use:   "service tournamentmgmt",
	Short: "Run tournament management service locally",
	RunE: func(cmd *cobra.Command, args []string) error {
		goals := []string{"spring-boot:run"}
		mvn := config.MavenExecutablePath(cfg.Paths.ServiceRoot, cfg.Build.MavenWrapper)
		cmdline := append([]string{mvn}, goals...)
		env := map[string]string{}
		if flagRunPort > 0 {
			env["SERVER_PORT"] = fmt.Sprintf("%d", flagRunPort)
		}
		if flagRunEnv != "" {
			env["SPRING_PROFILES_ACTIVE"] = flagRunEnv
		}
		slog.Info("exec", "cmd", cmdline, "cwd", cfg.Paths.ServiceRoot, "env", env)
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()
		code, _, _, err := execx.Run(ctx, execx.RunOptions{
			Cmd:           cmdline,
			Cwd:           cfg.Paths.ServiceRoot,
			Timeout:       0, // no timeout; user interrupts
			Interactive:   true,
			DryRun:        flagDryRun,
			CaptureOutput: false,
			Env:           env,
		})
		if err != nil {
			return fmt.Errorf("run failed (exit=%d): %w", code, err)
		}
		return nil
	},
}

func init() {
	runTournamentmgmtCmd.Flags().IntVar(&flagRunPort, "port", 8080, "server port")
	runTournamentmgmtCmd.Flags().StringVar(&flagRunEnv, "env", "dev", "spring profile")
	runCmd.AddCommand(runTournamentmgmtCmd)
	rootCmd.AddCommand(runCmd)
}
