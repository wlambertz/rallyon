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

var testCmd = &cobra.Command{
	Use:   "test",
	Short: "Run test suites",
	RunE: func(cmd *cobra.Command, args []string) error {
		mvn := config.MavenExecutablePath(cfg.Paths.ServiceRoot, cfg.Build.MavenWrapper)
		cmdline := []string{mvn, "test"}
		slog.Info("exec", "cmd", cmdline, "cwd", cfg.Paths.ServiceRoot)
		ctx, cancel := context.WithTimeout(context.Background(), 1*time.Hour)
		defer cancel()
		code, _, _, err := execx.Run(ctx, execx.RunOptions{
			Cmd:           cmdline,
			Cwd:           cfg.Paths.ServiceRoot,
			Timeout:       1 * time.Hour,
			Interactive:   false,
			DryRun:        flagDryRun,
			CaptureOutput: false,
		})
		if err != nil {
			return fmt.Errorf("tests failed (exit=%d): %w", code, err)
		}
		return nil
	},
}

func init() {
	rootCmd.AddCommand(testCmd)
}
