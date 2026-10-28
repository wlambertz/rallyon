package cmd

import (
	"bufio"
	"context"
	"errors"
	"fmt"
	"io"
	"os"
	"regexp"
	"strconv"
	"strings"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
)

const (
	defaultMinSubjectWords   = 3
	defaultBodyRequiredFiles = 3
	defaultBodyRequiredLines = 100
	maxSubjectLength         = 72
	minBodyLineLength        = 20
)

var (
	// gitCommitTypes is the Conventional Commits type list shared with `ro git commit`.
	gitCommitTypes = []string{"feat", "fix", "chore", "docs", "refactor", "test", "build", "ci", "perf", "style", "revert"}

	conventionalHeader = regexp.MustCompile(`^([a-z]+)(\([^)\s]+\))?(!)?: (.+)$`)

	// Each pattern exists because an agent or human actually wrote (or would
	// plausibly write) a message like it: it names no concrete change, so the
	// log stops being useful for review and bisecting.
	genericSubjectPatterns = []*regexp.Regexp{
		regexp.MustCompile(`(?i)^commit\b`),
		regexp.MustCompile(`(?i)^(update|change|modify|fix|tweak|adjust|improve|edit)\s+(the\s+)?(files?|code|stuff|things?|changes|content|everything|misc\w*)$`),
		regexp.MustCompile(`(?i)^(misc\w*|various|stuff|changes?|updates?|fixes|cleanup|clean up|wip|work in progress|save|checkpoint|sync|tmp|temp|test)$`),
		regexp.MustCompile(`(?i)^(address|apply|incorporate)\s+(review|feedback|comments)\b`),
		regexp.MustCompile(`(?i)^(current|latest|recent|pending|all|my|some)\s+(changes|work|files|updates)$`),
	}
)

// diffStats summarizes a staged diff so large changes can demand a body.
type diffStats struct {
	Files int
	Lines int
}

type lintOptions struct {
	MinSubjectWords   int
	BodyRequiredFiles int
	BodyRequiredLines int
	ExtraDeny         []*regexp.Regexp
}

func lintOptionsFromConfig(c config.CommitLint) (lintOptions, error) {
	opts := lintOptions{
		MinSubjectWords:   c.MinSubjectWords,
		BodyRequiredFiles: c.BodyRequiredFiles,
		BodyRequiredLines: c.BodyRequiredLines,
	}
	if opts.MinSubjectWords <= 0 {
		opts.MinSubjectWords = defaultMinSubjectWords
	}
	if opts.BodyRequiredFiles <= 0 {
		opts.BodyRequiredFiles = defaultBodyRequiredFiles
	}
	if opts.BodyRequiredLines <= 0 {
		opts.BodyRequiredLines = defaultBodyRequiredLines
	}
	for _, p := range c.ExtraDenyPatterns {
		re, err := regexp.Compile(p)
		if err != nil {
			return opts, fmt.Errorf("git.commitLint.extraDenyPatterns: %w", err)
		}
		opts.ExtraDeny = append(opts.ExtraDeny, re)
	}
	return opts, nil
}

// lintCommitMessage returns one problem per violated rule; empty means the
// message is acceptable. stats may be nil when no diff is available (CI ranges).
func lintCommitMessage(message string, stats *diffStats, opts lintOptions) []string {
	lines := strings.Split(strings.ReplaceAll(message, "\r\n", "\n"), "\n")

	// Drop git's "# ..." comment lines (present in commit-msg hook input).
	var kept []string
	for _, l := range lines {
		if strings.HasPrefix(l, "#") {
			continue
		}
		kept = append(kept, l)
	}
	subject := ""
	bodyStart := len(kept)
	for i, l := range kept {
		if strings.TrimSpace(l) != "" {
			subject = strings.TrimSpace(l)
			bodyStart = i + 1
			break
		}
	}
	if subject == "" {
		return []string{"message is empty"}
	}
	if isExemptSubject(subject) {
		return nil
	}

	var problems []string
	m := conventionalHeader.FindStringSubmatch(subject)
	var summary string
	if m == nil || !isKnownCommitType(m[1]) {
		problems = append(problems, fmt.Sprintf("subject must look like `type(scope): summary` with type one of %s", strings.Join(gitCommitTypes, ", ")))
		summary = subject
	} else {
		summary = strings.TrimSpace(m[4])
	}
	if len(subject) > maxSubjectLength {
		problems = append(problems, fmt.Sprintf("subject is %d characters; keep it at most %d", len(subject), maxSubjectLength))
	}
	if words := len(strings.Fields(summary)); words < opts.MinSubjectWords {
		problems = append(problems, fmt.Sprintf("summary has %d word(s); name the concrete change in at least %d", words, opts.MinSubjectWords))
	}
	for _, re := range append(append([]*regexp.Regexp{}, genericSubjectPatterns...), opts.ExtraDeny...) {
		if re.MatchString(summary) {
			problems = append(problems, fmt.Sprintf("summary %q is generic; say what changed and where (e.g. `gofmt build/config/run/test commands`)", summary))
			break
		}
	}

	if stats != nil && (stats.Files > opts.BodyRequiredFiles || stats.Lines > opts.BodyRequiredLines) && !hasSubstantialBody(kept[bodyStart:]) {
		problems = append(problems, fmt.Sprintf("change touches %d files / %d lines; add a body (a line of %d+ characters) explaining what and why", stats.Files, stats.Lines, minBodyLineLength))
	}
	return problems
}

func isKnownCommitType(t string) bool {
	for _, known := range gitCommitTypes {
		if known == t {
			return true
		}
	}
	return false
}

// Merge, fixup/squash/revert-generated and dependency-bot commits are produced
// by tools; rewriting them by hand would be noise, so they are exempt.
func isExemptSubject(subject string) bool {
	switch {
	case strings.HasPrefix(subject, "Merge "),
		strings.HasPrefix(subject, "fixup! "),
		strings.HasPrefix(subject, "squash! "),
		strings.HasPrefix(subject, "amend! "),
		strings.HasPrefix(subject, `Revert "`),
		strings.HasPrefix(subject, "chore(deps"),
		strings.HasPrefix(subject, "build(deps"):
		return true
	}
	return false
}

func hasSubstantialBody(lines []string) bool {
	for _, l := range lines {
		l = strings.TrimSpace(l)
		if strings.HasPrefix(strings.ToLower(l), "co-authored-by:") || strings.HasPrefix(strings.ToLower(l), "signed-off-by:") {
			continue
		}
		if len(l) >= minBodyLineLength {
			return true
		}
	}
	return false
}

// parseNumstat sums `git diff --numstat` output. Binary files report "-" and
// count as a file with zero lines.
func parseNumstat(out string) diffStats {
	var st diffStats
	sc := bufio.NewScanner(strings.NewReader(out))
	for sc.Scan() {
		f := strings.Fields(sc.Text())
		if len(f) < 3 {
			continue
		}
		st.Files++
		for _, n := range f[:2] {
			if v, err := strconv.Atoi(n); err == nil {
				st.Lines += v
			}
		}
	}
	return st
}

func stagedDiffStats(ctx context.Context) (*diffStats, error) {
	_, stdout, _, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           []string{"git", "diff", "--cached", "--numstat"},
		Cwd:           cfg.Project.Root,
		CaptureOutput: true,
	})
	if err != nil {
		return nil, err
	}
	st := parseNumstat(stdout)
	return &st, nil
}

func formatLintProblems(subject string, problems []string) string {
	var b strings.Builder
	fmt.Fprintf(&b, "commit message rejected: %q\n", subject)
	for _, p := range problems {
		fmt.Fprintf(&b, "  - %s\n", p)
	}
	b.WriteString("Good: `fix(deploy): handle dry-run mode when workflow input is missing`\n")
	b.WriteString("Bad:  `chore: commit current changes`, `update files`, `wip`\n")
	b.WriteString("See .agents/skills/commit-messages/SKILL.md for the recipe.")
	return b.String()
}

var (
	lintStdin bool
	lintRange string
)

func newGitLintMessageCmd() *cobra.Command {
	cmd := &cobra.Command{
		Use:   "lint-message [file]",
		Short: "Reject generic commit messages (commit-msg hook, agents, CI)",
		Long: `Validate commit messages against RallyOn's rules: Conventional Commit header,
concrete (non-generic) summary, and a body for large changes.

Inputs: a message file (the commit-msg hook passes one), --stdin, or
--range base..head to check every commit in a range (used by CI).`,
		Args: cobra.MaximumNArgs(1),
		RunE: runGitLintMessage,
		// A rejected message is a verdict, not a usage error; usage text would bury the guidance.
		SilenceUsage: true,
	}
	cmd.Flags().BoolVar(&lintStdin, "stdin", false, "read the message from stdin")
	cmd.Flags().StringVar(&lintRange, "range", "", "lint every commit in base..head")
	return cmd
}

func runGitLintMessage(cmd *cobra.Command, args []string) error {
	if cfg == nil {
		return errors.New("configuration not loaded")
	}
	ctx := cmd.Context()
	if ctx == nil {
		ctx = context.Background()
	}
	opts, err := lintOptionsFromConfig(cfg.Git.CommitLint)
	if err != nil {
		return err
	}

	if lintRange != "" {
		return lintCommitRange(ctx, lintRange, opts)
	}

	var raw []byte
	switch {
	case lintStdin:
		raw, err = io.ReadAll(cmd.InOrStdin())
	case len(args) == 1:
		raw, err = os.ReadFile(args[0])
	default:
		return errors.New("provide a message file, --stdin, or --range")
	}
	if err != nil {
		return err
	}
	// Stats come from the index, which is what a commit-msg hook is about to commit.
	// Outside a repo with staged changes (e.g. a pre-flight --stdin check) they are
	// simply absent and the body rule is skipped.
	stats, statsErr := stagedDiffStats(ctx)
	if statsErr != nil {
		stats = nil
	}
	return reportLint(string(raw), lintCommitMessage(string(raw), stats, opts))
}

func lintCommitRange(ctx context.Context, rng string, opts lintOptions) error {
	_, stdout, stderr, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           []string{"git", "log", "--no-merges", "--format=%H", rng},
		Cwd:           cfg.Project.Root,
		CaptureOutput: true,
	})
	if err != nil {
		return fmt.Errorf("git log %s: %w (%s)", rng, err, strings.TrimSpace(stderr))
	}
	failed := 0
	for _, sha := range strings.Fields(stdout) {
		_, msg, _, err := execx.Run(ctx, execx.RunOptions{
			Cmd:           []string{"git", "show", "-s", "--format=%B", sha},
			Cwd:           cfg.Project.Root,
			CaptureOutput: true,
		})
		if err != nil {
			return err
		}
		_, numstat, _, err := execx.Run(ctx, execx.RunOptions{
			Cmd:           []string{"git", "show", "--format=", "--numstat", sha},
			Cwd:           cfg.Project.Root,
			CaptureOutput: true,
		})
		if err != nil {
			return err
		}
		st := parseNumstat(numstat)
		if problems := lintCommitMessage(msg, &st, opts); len(problems) > 0 {
			failed++
			fmt.Fprintf(os.Stderr, "%s\n%s\n\n", sha[:10], formatLintProblems(firstLine(msg), problems))
		}
	}
	if failed > 0 {
		return fmt.Errorf("%d commit(s) in %s have rejected messages", failed, rng)
	}
	return nil
}

func reportLint(message string, problems []string) error {
	if len(problems) == 0 {
		return nil
	}
	return errors.New(formatLintProblems(firstLine(message), problems))
}

func firstLine(msg string) string {
	for _, l := range strings.Split(msg, "\n") {
		if t := strings.TrimSpace(l); t != "" && !strings.HasPrefix(t, "#") {
			return t
		}
	}
	return ""
}

func newGitHooksCmd() *cobra.Command {
	hooks := &cobra.Command{
		Use:   "hooks",
		Short: "Manage repository git hooks",
	}
	hooks.AddCommand(&cobra.Command{
		Use:   "install",
		Short: "Point git at .githooks so commit-msg linting runs locally",
		RunE: func(cmd *cobra.Command, args []string) error {
			return runGitCommand(cmd.Context(), []string{"git", "config", "core.hooksPath", ".githooks"})
		},
	})
	return hooks
}

func init() {
	gitCmd.AddCommand(newGitLintMessageCmd())
	gitCmd.AddCommand(newGitHooksCmd())
}
