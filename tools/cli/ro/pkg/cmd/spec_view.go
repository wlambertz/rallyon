package cmd

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

// specViewCmd represents the view command for OpenSpec specs
var specViewCmd = &cobra.Command{
	Use:   "view [spec-file]",
	Short: "View an OpenSpec specification file contents",
	Long: `Display the contents of an OpenSpec specification file.

The command reads and displays the spec file content, supporting YAML, JSON,
and TOON formats. This is useful for inspecting spec files.

Example:
  ro spec view ./docs/wiki/specs/my-feature-auth.yaml
  ro spec view ./docs/wiki/specs/my-feature.json
  ro spec view ./docs/wiki/specs/my-feature.toon`,
	Args: cobra.ExactArgs(1),
	RunE: func(cmd *cobra.Command, args []string) error {
		specFile := args[0]

		// Read the spec file
		specData, err := os.ReadFile(specFile)
		if err != nil {
			return fmt.Errorf("failed to read spec file: %w", err)
		}

		// Display the spec content
		fmt.Printf("OpenSpec Specification: %s\n", specFile)
		fmt.Printf("========================\n\n")
		fmt.Printf("%s\n", string(specData))

		return nil
	},
}

func init() {
	rootCmd.AddCommand(specViewCmd)
}
