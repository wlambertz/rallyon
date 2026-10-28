package cmd

import (
	"context"
	"fmt"
	"log/slog"
	"time"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
)

var (
	flagBuildFast bool
	flagBuildCI   bool
)

var buildCmd = &cobra.Command{
	Use:   "build",
	Short: "Build the project or specific services",
	RunE: func(cmd *cobra.Command, args []string) error {
		goals := make([]string, 0, len(cfg.Build.DefaultGoals))
		goals = append(goals, cfg.Build.DefaultGoals...)
		if flagBuildFast {
			// replace verify with -DskipTests where applicable
			goals = []string{"clean", "package", "-DskipTests"}
		}
		if flagBuildCI {
			// CI-friendly clean verify
			goals = []string{"clean", "verify", "-B"}
		}

		mvn := config.MavenExecutablePath(cfg.Paths.ServiceRoot, cfg.Build.MavenWrapper)
		cmdline := append([]string{mvn}, goals...)
		slog.Info("exec", "cmd", cmdline, "cwd", cfg.Paths.ServiceRoot)
		ctx, cancel := context.WithTimeout(context.Background(), 2*time.Hour)
		defer cancel()
		code, _, _, err := execx.Run(ctx, execx.RunOptions{
			Cmd:           cmdline,
			Cwd:           cfg.Paths.ServiceRoot,
			Timeout:       2 * time.Hour,
			Interactive:   false,
			DryRun:        flagDryRun,
			CaptureOutput: false,
		})
		if err != nil {
			return fmt.Errorf("build failed (exit=%d): %w", code, err)
		}
		return nil
	},
}

func init() {
	buildCmd.Flags().BoolVar(&flagBuildFast, "fast", false, "skip tests for faster build")
	buildCmd.Flags().BoolVar(&flagBuildCI, "ci", false, "CI mode (batch, non-interactive)")
	rootCmd.AddCommand(buildCmd)
}
