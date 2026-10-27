package cmd

import (
	"bytes"
	"context"
	"errors"
	"fmt"
	"log/slog"
	"os"
	"path/filepath"
	"sort"
	"strings"
	"time"

	"github.com/spf13/cobra"
	"github.com/spf13/pflag"
)

var docsCmd = &cobra.Command{
	Use:   "docs",
	Short: "Documentation utilities",
}

var (
	flagDocsOutput    string
	flagDocsWiki      bool
	flagDocsWikiPath  string
	flagDocsCommitMsg string
	flagDocsPublish   bool
)

var docsGenerateCmd = &cobra.Command{
	Use:   "generate",
	Short: "Generate CLI reference markdown",
	RunE: func(cmd *cobra.Command, args []string) error {
		if flagDocsPublish {
			flagDocsWiki = true
		}
		if cfg == nil {
			return fmt.Errorf("configuration not loaded")
		}
		content, err := renderDocs(rootCmd)
		if err != nil {
			return err
		}

		outputPath := resolveDocsPath(flagDocsOutput, cfg.Docs.Output, filepath.Join("docs", "cli-reference.md"))
		if err := writeDocsFile(outputPath, content); err != nil {
			return err
		}

		shouldWriteWiki := flagDocsWiki || cfg.Docs.PublishToWiki
		if shouldWriteWiki {
			wikiPath := resolveDocsPath(flagDocsWikiPath, cfg.Docs.WikiOutput, filepath.Join("docs", "wiki", "CLI.md"))
			if err := writeDocsFile(wikiPath, content); err != nil {
				return err
			}
			slog.Info("docs written to wiki; remember to commit docs/wiki changes", "path", wikiPath)
			if cfg.Docs.AutoStageWiki || flagDocsPublish {
				if err := stageWikiFile(wikiPath); err != nil {
					return err
				}
			}
			if flagDocsPublish {
				message := flagDocsCommitMsg
				if message == "" {
					message = cfg.Docs.WikiCommitMessage
				}
				if err := commitWiki(message); err != nil {
					return err
				}
			}
		}
		return nil
	},
}

func init() {
	docsGenerateCmd.Flags().StringVar(&flagDocsOutput, "output", "", "file to write (defaults to docs.output)")
	docsGenerateCmd.Flags().BoolVar(&flagDocsWiki, "wiki", false, "also write to wiki docs file")
	docsGenerateCmd.Flags().StringVar(&flagDocsWikiPath, "wiki-output", "", "wiki docs path (defaults to docs.wikiOutput)")
	docsGenerateCmd.Flags().StringVar(&flagDocsCommitMsg, "commit-message", "", "commit message when publishing to wiki (overrides docs.wikiCommitMessage)")
	docsGenerateCmd.Flags().BoolVar(&flagDocsPublish, "commit-wiki", false, "commit wiki changes after generation")
	docsCmd.AddCommand(docsGenerateCmd)
	rootCmd.AddCommand(docsCmd)
}

func renderDocs(root *cobra.Command) (string, error) {
	var buf bytes.Buffer
	generatedAt := time.Now().Format(time.RFC3339)
	buf.WriteString("# RallyOn CLI Reference\n\n")
	buf.WriteString(fmt.Sprintf("_Generated on %s_\n\n", generatedAt))

	cmds := collectCommands(root)
	if len(cmds) == 0 {
		return "", errors.New("no commands found for documentation")
	}

	buf.WriteString("## Table of Contents\n\n")
	for _, c := range cmds {
		anchor := anchorFor(c.CommandPath())
		buf.WriteString(fmt.Sprintf("- [%s](#%s)\n", c.CommandPath(), anchor))
	}
	buf.WriteString("\n")

	for _, c := range cmds {
		writeCommandSection(&buf, c)
	}
	return buf.String(), nil
}

func stageWikiFile(path string) error {
	// Paths are relative to the project root (see writeDocsFile), so stage from there
	// regardless of the CWD the CLI was started in.
	if err := runGitCommand(context.Background(), []string{"git", "-C", cfg.Project.Root, "add", "--", path}); err != nil {
		return fmt.Errorf("stage wiki file: %w", err)
	}
	slog.Info("wiki staged", "file", path)
	return nil
}

func commitWiki(message string) error {
	wikiDir := filepath.Join(cfg.Project.Root, "docs", "wiki")
	// docs/wiki is a plain directory now, so a bare `git commit` would also commit unrelated
	// staged changes; the pathspec limits the commit to wiki content.
	args := []string{"git", "-C", wikiDir, "commit", "-m", message, "--", "."}
	if err := runGitCommand(context.Background(), args); err != nil {
		return fmt.Errorf("commit wiki: %w", err)
	}
	slog.Info("wiki committed", "message", message)
	return nil
}

func collectCommands(root *cobra.Command) []*cobra.Command {
	var result []*cobra.Command
	var walk func(*cobra.Command)
	walk = func(c *cobra.Command) {
		result = append(result, c)
		children := make([]*cobra.Command, 0, len(c.Commands()))
		for _, child := range c.Commands() {
			if !child.IsAvailableCommand() || child.Hidden {
				continue
			}
			children = append(children, child)
		}
		sort.Slice(children, func(i, j int) bool {
			return children[i].Name() < children[j].Name()
		})
		for _, child := range children {
			walk(child)
		}
	}
	walk(root)
	return result
}

func writeCommandSection(buf *bytes.Buffer, c *cobra.Command) {
	buf.WriteString(fmt.Sprintf("## %s\n\n", c.CommandPath()))
	if short := strings.TrimSpace(c.Short); short != "" {
		buf.WriteString(short + "\n\n")
	}
	if long := strings.TrimSpace(c.Long); long != "" && long != c.Short {
		buf.WriteString(long + "\n\n")
	}
	buf.WriteString("```bash\n")
	buf.WriteString(c.UseLine())
	buf.WriteString("\n```\n\n")

	if aliases := c.Aliases; len(aliases) > 0 {
		buf.WriteString("**Aliases:** " + strings.Join(aliases, ", ") + "\n\n")
	}
	if example := strings.TrimSpace(c.Example); example != "" {
		buf.WriteString("**Examples**\n\n")
		buf.WriteString("```bash\n")
		buf.WriteString(example)
		buf.WriteString("\n```\n\n")
	}
	writeFlagSection(buf, "Flags", c.NonInheritedFlags())
	writeFlagSection(buf, "Inherited Flags", c.InheritedFlags())
}

func writeFlagSection(buf *bytes.Buffer, title string, flags *pflag.FlagSet) {
	if flags == nil || !flags.HasAvailableFlags() {
		return
	}
	buf.WriteString(fmt.Sprintf("**%s**\n\n", title))
	flagText := strings.TrimSpace(flags.FlagUsagesWrapped(100))
	if flagText == "" {
		return
	}
	buf.WriteString("```\n")
	buf.WriteString(flagText)
	buf.WriteString("\n```\n\n")
}

func anchorFor(value string) string {
	value = strings.ToLower(value)
	value = strings.ReplaceAll(value, " ", "-")
	value = strings.ReplaceAll(value, "/", "-")
	value = strings.ReplaceAll(value, ":", "")
	return value
}

func resolveDocsPath(flagValue string, configValue string, fallback string) string {
	if strings.TrimSpace(flagValue) != "" {
		return flagValue
	}
	if strings.TrimSpace(configValue) != "" {
		return configValue
	}
	return fallback
}

func writeDocsFile(path string, content string) error {
	if strings.TrimSpace(path) == "" {
		return fmt.Errorf("no output path specified")
	}
	full := filepath.Join(cfg.Project.Root, path)
	if err := os.MkdirAll(filepath.Dir(full), 0o755); err != nil {
		return fmt.Errorf("create docs dir: %w", err)
	}
	if err := os.WriteFile(full, []byte(content), 0o644); err != nil {
		return fmt.Errorf("write docs: %w", err)
	}
	slog.Info("docs generated", "path", path)
	return nil
}
