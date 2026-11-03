package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

const legacySpecWarning = "Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md."

func registerSpecCommands(root *cobra.Command) {
	spec := &cobra.Command{
		Use:   "spec",
		Short: "Legacy YAML specification helpers (deprecated)",
		Long:  legacySpecWarning,
	}
	for _, factory := range []func() *cobra.Command{newSpecCreateCommand, newSpecValidateCommand, newSpecViewCommand} {
		for _, parent := range []*cobra.Command{root, spec} {
			leaf := factory()
			leaf.Long = legacySpecWarning + "\n\n" + leaf.Long
			validateArgs := leaf.Args
			// Warn before argument and inherited configuration checks can fail.
			leaf.Args = func(cmd *cobra.Command, args []string) error {
				fmt.Fprintln(cmd.ErrOrStderr(), legacySpecWarning)
				return validateArgs(cmd, args)
			}
			leaf.SetFlagErrorFunc(func(cmd *cobra.Command, err error) error {
				fmt.Fprintln(cmd.ErrOrStderr(), legacySpecWarning)
				return err
			})
			parent.AddCommand(leaf)
		}
	}
	root.AddCommand(spec)
}

func init() {
	registerSpecCommands(rootCmd)
}
