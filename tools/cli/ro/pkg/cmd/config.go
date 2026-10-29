package cmd

import (
	"encoding/json"
	"fmt"
	"log/slog"
	"os"
	"path/filepath"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
)

var configCmd = &cobra.Command{
	Use:   "config",
	Short: "Configuration commands",
}

var configShowCmd = &cobra.Command{
	Use:   "show",
	Short: "Show merged configuration",
	RunE: func(cmd *cobra.Command, args []string) error {
		cwd, _ := os.Getwd()
		repoRoot := findRepoRootFrom(cwd)
		cfg, err := config.Load(repoRoot)
		if err != nil {
			return err
		}
		if flagJSON {
			enc := json.NewEncoder(os.Stdout)
			enc.SetIndent("", "  ")
			return enc.Encode(cfg)
		}
		// Human-friendly
		slog.Info("project", "name", cfg.Project.Name, "root", cfg.Project.Root)
		fmt.Printf("serviceRoot: %s\n", cfg.Paths.ServiceRoot)
		fmt.Printf("mavenWrapper: %s\n", cfg.Build.MavenWrapper)
		fmt.Printf("defaultGoals: %v\n", cfg.Build.DefaultGoals)
		fmt.Printf("output.verbose: %v\n", cfg.Output.Verbose)
		fmt.Printf("output.json: %v\n", cfg.Output.JSON)
		return nil
	},
}

func init() {
	configCmd.AddCommand(configShowCmd)
	rootCmd.AddCommand(configCmd)
}

func findRepoRootFrom(start string) string {
	dir := start
	for {
		if _, err := os.Stat(filepath.Join(dir, ".git")); err == nil {
			return dir
		}
		parent := filepath.Dir(dir)
		if parent == dir {
			return ""
		}
		dir = parent
	}
}
