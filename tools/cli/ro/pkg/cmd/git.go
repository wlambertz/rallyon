package cmd

import (
	"context"
	"errors"
	"fmt"
	"log/slog"
	"strings"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/prompt"
)

var (
	gitStatusVerbose bool
	gitBranchVerbose bool

	gitRebaseTarget    string
	gitRebaseAutostash bool

	commitType         string
	commitScope        string
	commitSummary      string
	commitBody         string
	commitBreaking     bool
	commitBreakingNote string
	commitWip          bool
	commitAll          bool
)

var gitCmd = &cobra.Command{
	Use:   "git",
	Short: "Git helper commands (status, branch, rebase, commit)",
}

func init() {
	gitStatusCmd := &cobra.Command{
		Use:   "status",
		Short: "Show git status (short by default)",
		RunE: func(cmd *cobra.Command, args []string) error {
			gitArgs := []string{"git", "status"}
			if !gitStatusVerbose {
				gitArgs = append(gitArgs, "-sb")
			}
			return runGitCommand(cmd.Context(), gitArgs)
		},
	}
	gitStatusCmd.Flags().BoolVar(&gitStatusVerbose, "long", false, "show long git status output")

	gitBranchCmd := &cobra.Command{
		Use:   "branch",
		Short: "Display branch information and ahead/behind summary",
		RunE: func(cmd *cobra.Command, args []string) error {
			gitArgs := []string{"git", "status", "-sb", "--branch"}
			if err := runGitCommand(cmd.Context(), gitArgs); err != nil {
				return err
			}
			if gitBranchVerbose {
				return runGitCommand(cmd.Context(), []string{"git", "branch", "-vv"})
			}
			return nil
		},
	}
	gitBranchCmd.Flags().BoolVar(&gitBranchVerbose, "verbose", false, "show verbose branch listing")

	gitRebaseCmd := &cobra.Command{
		Use:   "rebase",
		Short: "Pull with rebase against the specified upstream",
		RunE: func(cmd *cobra.Command, args []string) error {
			remote, branch := parseRemoteBranch(gitRebaseTarget)
			pullArgs := []string{"git", "pull", "--rebase"}
			if gitRebaseAutostash {
				pullArgs = append(pullArgs, "--autostash")
			}
			pullArgs = append(pullArgs, remote, branch)
			slog.Info("git rebase", "remote", remote, "branch", branch)
			return runGitCommand(cmd.Context(), pullArgs)
		},
	}
	gitRebaseCmd.Flags().StringVar(&gitRebaseTarget, "onto", "origin/main", "upstream (remote/branch) to rebase against")
	gitRebaseCmd.Flags().BoolVar(&gitRebaseAutostash, "autostash", true, "auto-stash local changes during rebase")

	gitCommitCmd := &cobra.Command{
		Use:   "commit",
		Short: "Guide conventional commits",
		RunE:  runGitCommit,
	}
	gitCommitCmd.Flags().StringVar(&commitType, "type", "", "commit type (feat, fix, chore, ...)")
	gitCommitCmd.Flags().StringVar(&commitScope, "scope", "", "optional commit scope")
	gitCommitCmd.Flags().StringVar(&commitSummary, "summary", "", "short summary line")
	gitCommitCmd.Flags().StringVar(&commitBody, "body", "", "body/description (supports newlines)")
	gitCommitCmd.Flags().BoolVar(&commitBreaking, "breaking", false, "mark commit as breaking change (adds '!')")
	gitCommitCmd.Flags().StringVar(&commitBreakingNote, "breaking-notes", "", "details for BREAKING CHANGE footer")
	gitCommitCmd.Flags().BoolVar(&commitWip, "wip", false, "mark commit as work in progress")
	gitCommitCmd.Flags().BoolVar(&commitAll, "all", false, "stage tracked files before committing (git add -A)")

	gitCmd.AddCommand(gitStatusCmd)
	gitCmd.AddCommand(gitBranchCmd)
	gitCmd.AddCommand(gitRebaseCmd)
	gitCmd.AddCommand(gitCommitCmd)
	gitCmd.AddCommand(newGitPushCmd())
	gitCmd.AddCommand(newGitSyncCmd())
	rootCmd.AddCommand(gitCmd)
}

func runGitCommand(ctx context.Context, args []string) error {
	if ctx == nil {
		ctx = context.Background()
	}
	_, stdout, stderr, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           args,
		Cwd:           cfg.Project.Root,
		CaptureOutput: true,
	})
	if stdout != "" {
		fmt.Print(stdout)
	}
	if err != nil {
		if stderr != "" {
			fmt.Print(stderr)
		}
		return err
	}
	return nil
}

func parseRemoteBranch(value string) (string, string) {
	value = strings.TrimSpace(value)
	if value == "" {
		return "origin", "main"
	}
	parts := strings.SplitN(value, "/", 2)
	if len(parts) == 2 {
		if parts[0] == "" {
			return "origin", parts[1]
		}
		return parts[0], parts[1]
	}
	return "origin", parts[0]
}

func runGitCommit(cmd *cobra.Command, args []string) error {
	if cfg == nil {
		return errors.New("configuration not loaded")
	}
	ctx := cmd.Context()
	if ctx == nil {
		ctx = context.Background()
	}

	if commitAll {
		if err := runGitCommand(ctx, []string{"git", "add", "-A"}); err != nil {
			return err
		}
	}
	if err := ensureStagedChanges(ctx); err != nil {
		return err
	}

	inputs := commitInputs{
		Type:         commitType,
		Scope:        commitScope,
		Summary:      commitSummary,
		Body:         commitBody,
		Breaking:     commitBreaking,
		BreakingNote: commitBreakingNote,
		Wip:          commitWip,
	}
	if err := promptForCommit(&inputs); err != nil {
		return err
	}

	msg, err := buildCommitMessage(inputs)
	if err != nil {
		return err
	}

	lintOpts, err := lintOptionsFromConfig(cfg.Git.CommitLint)
	if err != nil {
		return err
	}
	stats, statsErr := stagedDiffStats(ctx)
	if statsErr != nil {
		stats = nil
	}
	if problems := lintCommitMessage(strings.Join(append([]string{msg.Subject}, msg.Bodies...), "\n\n"), stats, lintOpts); len(problems) > 0 {
		return errors.New(formatLintProblems(msg.Subject, problems))
	}

	fmt.Println("-------- Commit Preview --------")
	fmt.Println(msg.Subject)
	for _, body := range msg.Bodies {
		fmt.Println()
		fmt.Println(body)
	}
	fmt.Println("--------------------------------")

	if !flagYes && !prompt.Confirm("Proceed with git commit?", true) {
		slog.Info("commit aborted by user")
		return nil
	}

	gitArgs := []string{"git", "commit", "-m", msg.Subject}
	for _, body := range msg.Bodies {
		gitArgs = append(gitArgs, "-m", body)
	}
	_, _, stderr, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           gitArgs,
		Cwd:           cfg.Project.Root,
		CaptureOutput: true,
	})
	if err != nil {
		if stderr != "" {
			fmt.Print(stderr)
		}
		return err
	}
	return nil
}

func newGitPushCmd() *cobra.Command {
	var (
		flagPushForce  bool
		flagPushRemote string
		flagPushBranch string
	)
	cmd := &cobra.Command{
		Use:   "push",
		Short: "Push current branch to configured remote (with safeguards)",
		RunE: func(cmd *cobra.Command, args []string) error {
			if cfg == nil {
				return errors.New("configuration not loaded")
			}
			ctx := cmd.Context()
			if ctx == nil {
				ctx = context.Background()
			}
			if err := ensureCleanWorktree(ctx); err != nil {
				return err
			}
			remote := flagPushRemote
			if remote == "" {
				remote = cfg.Git.DefaultRemote
			}
			branch := flagPushBranch
			if branch == "" {
				var err error
				branch, err = gitRevParseFn(ctx, "--abbrev-ref", "HEAD")
				if err != nil {
					return err
				}
			}
			if branch == "" {
				return errors.New("could not determine current branch")
			}
			if flagPushForce && !flagYes {
				if !prompt.Confirm(fmt.Sprintf("Force push %s to %s?", branch, remote), false) {
					return nil
				}
			}
			gitArgs := []string{"git", "push", remote, branch}
			if flagPushForce {
				gitArgs = append(gitArgs, "--force-with-lease")
			}
			slog.Info("git push", "remote", remote, "branch", branch, "force", flagPushForce)
			return runGitCommand(ctx, gitArgs)
		},
	}
	cmd.Flags().BoolVar(&flagPushForce, "force", false, "use --force-with-lease")
	cmd.Flags().StringVar(&flagPushRemote, "remote", "", "remote name (defaults to git.defaultRemote)")
	cmd.Flags().StringVar(&flagPushBranch, "branch", "", "branch to push (defaults to current)")
	return cmd
}

func newGitSyncCmd() *cobra.Command {
	var flagSyncRemote string
	var flagSyncBranch string
	var flagSyncAutostash bool
	cmd := &cobra.Command{
		Use:   "sync",
		Short: "Fetch and rebase current branch onto upstream",
		RunE: func(cmd *cobra.Command, args []string) error {
			if cfg == nil {
				return errors.New("configuration not loaded")
			}
			ctx := cmd.Context()
			if ctx == nil {
				ctx = context.Background()
			}
			remote := flagSyncRemote
			if remote == "" {
				remote = cfg.Git.DefaultRemote
			}
			branch := flagSyncBranch
			if branch == "" {
				branch = cfg.Git.DefaultBranch
			}
			fetchArgs := []string{"git", "fetch", remote, branch}
			if err := runGitCommand(ctx, fetchArgs); err != nil {
				return err
			}
			pullArgs := []string{"git", "pull", "--rebase"}
			if flagSyncAutostash {
				pullArgs = append(pullArgs, "--autostash")
			}
			pullArgs = append(pullArgs, remote, branch)
			slog.Info("git sync", "remote", remote, "branch", branch, "autostash", flagSyncAutostash)
			return runGitCommand(ctx, pullArgs)
		},
	}
	cmd.Flags().StringVar(&flagSyncRemote, "remote", "", "upstream remote (defaults to git.defaultRemote)")
	cmd.Flags().StringVar(&flagSyncBranch, "branch", "", "upstream branch (defaults to git.defaultBranch)")
	cmd.Flags().BoolVar(&flagSyncAutostash, "autostash", true, "auto stash during rebase")
	return cmd
}

func promptForCommit(in *commitInputs) error {
	conventionalTypes := []string{"feat", "fix", "chore", "docs", "refactor", "test", "build", "ci", "perf", "style"}

	in.Type = strings.TrimSpace(strings.ToLower(in.Type))
	if in.Type == "" {
		defaultType := "chore"
		if cfg.Git.ConventionalCommits {
			defaultType = "feat"
		}
		in.Type = prompt.Input(fmt.Sprintf("Commit type (%s)", strings.Join(conventionalTypes, "/")), defaultType)
	}

	in.Scope = strings.TrimSpace(in.Scope)
	if cfg.Git.ConventionalCommits && strings.Contains(in.Scope, " ") {
		return errors.New("scope must not contain spaces when conventional commits are enforced")
	}

	in.Summary = strings.TrimSpace(in.Summary)
	if in.Summary == "" {
		in.Summary = prompt.Input("Summary", "")
	}

	if in.Body == "" {
		in.Body = prompt.Input("Details/body (optional)", "")
	}

	if in.Breaking && in.BreakingNote == "" {
		in.BreakingNote = prompt.Input("Breaking change notes", "")
	}

	return nil
}

type commitInputs struct {
	Type         string
	Scope        string
	Summary      string
	Body         string
	Breaking     bool
	BreakingNote string
	Wip          bool
}

type commitMessage struct {
	Subject string
	Bodies  []string
}

func buildCommitMessage(in commitInputs) (commitMessage, error) {
	msg := commitMessage{}
	commitType := strings.TrimSpace(in.Type)
	if commitType == "" {
		return msg, errors.New("commit type is required")
	}
	summary := strings.TrimSpace(in.Summary)
	if summary == "" {
		return msg, errors.New("commit summary is required")
	}
	if in.Wip {
		summary = "[WIP] " + summary
	}

	header := commitType
	scope := strings.TrimSpace(in.Scope)
	if scope != "" {
		header = fmt.Sprintf("%s(%s)", header, scope)
	}
	if in.Breaking {
		header += "!"
	}
	msg.Subject = fmt.Sprintf("%s: %s", header, summary)

	body := strings.TrimSpace(in.Body)
	if body != "" {
		msg.Bodies = append(msg.Bodies, body)
	}
	if in.Breaking && strings.TrimSpace(in.BreakingNote) != "" {
		msg.Bodies = append(msg.Bodies, fmt.Sprintf("BREAKING CHANGE: %s", strings.TrimSpace(in.BreakingNote)))
	}
	return msg, nil
}

func ensureStagedChanges(ctx context.Context) error {
	_, stdout, _, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           []string{"git", "diff", "--cached", "--name-only"},
		Cwd:           cfg.Project.Root,
		CaptureOutput: true,
	})
	if err != nil {
		return err
	}
	if strings.TrimSpace(stdout) == "" {
		return errors.New("no staged changes detected (use git add ... or run with --all)")
	}
	return nil
}

func ensureCleanWorktree(ctx context.Context) error {
	status, err := gitStatusFn(ctx)
	if err != nil {
		return err
	}
	if strings.TrimSpace(status) == "" {
		return nil
	}
	if flagYes {
		slog.Warn("working tree has uncommitted changes; continuing due to --yes")
		return nil
	}
	return errors.New("working tree has uncommitted changes; commit/stash or use --yes to override")
}
