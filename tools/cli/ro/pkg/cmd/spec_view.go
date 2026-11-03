package cmd

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

func newSpecViewCommand() *cobra.Command {
	return &cobra.Command{
		Use:   "view [spec-file]",
		Short: "Display raw legacy specification contents (deprecated)",
		Long: `Display the contents of an OpenSpec specification file.

The command reads and displays the spec file content, supporting YAML, JSON,
and TOON formats. This is useful for inspecting spec files.

Example:
  ro spec view /path/to/legacy.yaml
  ro spec view /path/to/legacy.json
  ro spec view /path/to/legacy.toon`,
		Args: cobra.ExactArgs(1),
		RunE: func(cmd *cobra.Command, args []string) error {
			specFile := args[0]

			// Read the spec file
			specData, err := os.ReadFile(specFile)
			if err != nil {
				return fmt.Errorf("failed to read spec file: %w", err)
			}

			// Display the spec content
			fmt.Fprintf(cmd.OutOrStdout(), "OpenSpec Specification: %s\n", specFile)
			fmt.Fprintf(cmd.OutOrStdout(), "========================\n\n")
			fmt.Fprintf(cmd.OutOrStdout(), "%s\n", string(specData))

			return nil
		},
	}

}
