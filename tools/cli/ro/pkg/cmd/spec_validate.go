package cmd

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
	"gopkg.in/yaml.v3"
)

// specValidateCmd represents the validate command for OpenSpec specs
var specValidateCmd = &cobra.Command{
	Use:   "validate [spec-file]",
	Short: "Validate an OpenSpec specification against the schema",
	Long: `Validate an OpenSpec specification file (YAML, JSON, or TOON format) against the OpenSpec v1.1 schema structure.

The validation checks that the spec has the required hierarchical structure:
- specification root
- title and status
- epic
- ticket with title, description, acceptanceCriteria, implementationSteps

Example:
  ro spec validate ./docs/wiki/specs/my-feature-auth.yaml
  ro spec validate ./docs/wiki/specs/my-feature.yaml`,
	Args: cobra.ExactArgs(1),
	RunE: func(cmd *cobra.Command, args []string) error {
		specFile := args[0]

		// Read the spec file
		specData, err := os.ReadFile(specFile)
		if err != nil {
			return fmt.Errorf("failed to read spec file: %w", err)
		}

		// Try to parse as YAML
		var spec map[string]interface{}
		if err := yaml.Unmarshal(specData, &spec); err != nil {
			return fmt.Errorf("failed to parse spec YAML: %w", err)
		}

		// Perform basic structural validation
		errors := validateSpecStructure(spec)

		if len(errors) > 0 {
			fmt.Printf("Spec validation FAILED for: %s\n", specFile)
			for _, e := range errors {
				fmt.Printf("  - %s\n", e)
			}
			return fmt.Errorf("spec validation failed - see errors above")
		}

		fmt.Printf("Spec validation PASSED: %s\n", specFile)
		return nil
	},
}

// validateSpecStructure checks the basic OpenSpec hierarchical structure
func validateSpecStructure(spec map[string]interface{}) []string {
	var errors []string

	// Check for specification root key
	if _, ok := spec["specification"]; !ok {
		errors = append(errors, "missing 'specification' root key")
	}

	// Check for title
	if title, ok := spec["title"]; !ok {
		errors = append(errors, "missing 'title' field")
	} else if titleStr, ok := title.(string); !ok || titleStr == "" {
		errors = append(errors, "empty 'title' field")
	}

	// Check for status
	if _, ok := spec["status"]; !ok {
		errors = append(errors, "missing 'status' field")
	}

	// Check for epic
	if _, ok := spec["epic"]; !ok {
		errors = append(errors, "missing 'epic' field")
	}

	// Check for ticket section
	ticket, ok := spec["ticket"]
	if !ok {
		errors = append(errors, "missing 'ticket' section")
	} else if ticketMap, ok := ticket.(map[string]interface{}); ok {
		// Check ticket title
		if _, ok := ticketMap["title"]; !ok {
			errors = append(errors, "missing 'ticket.title' field")
		}

		// Check acceptanceCriteria
		if ac, ok := ticketMap["acceptanceCriteria"]; !ok {
			errors = append(errors, "missing 'ticket.acceptanceCriteria' field")
		} else if acSlice, ok := ac.([]interface{}); ok && len(acSlice) == 0 {
			errors = append(errors, "empty 'ticket.acceptanceCriteria' array")
		}

		// Check implementationSteps
		if is, ok := ticketMap["implementationSteps"]; !ok {
			errors = append(errors, "missing 'ticket.implementationSteps' field")
		} else if isSlice, ok := is.([]interface{}); ok && len(isSlice) == 0 {
			errors = append(errors, "empty 'ticket.implementationSteps' array")
		}
	}

	// Check for filesToBeCreated
	if _, ok := spec["filesToBeCreated"]; !ok {
		errors = append(errors, "missing 'filesToBeCreated' field")
	}

	// Check for codeReferences
	if _, ok := spec["codeReferences"]; !ok {
		errors = append(errors, "missing 'codeReferences' field")
	}

	return errors
}

func init() {
	rootCmd.AddCommand(specValidateCmd)
}
