package cmd

import (
	"strings"
	"testing"

	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
)

func TestLintCommitMessage(t *testing.T) {
	opts, err := lintOptionsFromConfig(config.CommitLint{})
	if err != nil {
		t.Fatal(err)
	}
	large := &diffStats{Files: 7, Lines: 237}

	tests := []struct {
		name     string
		message  string
		stats    *diffStats
		wantFail string // substring of a problem; empty means must pass
	}{
		{"descriptive subject", "style(ro): gofmt cmd files and add yaml.v3 dependency", nil, ""},
		{"short but concrete", "feat: add deploy command", nil, ""},
		{"the generic message that landed on main", "chore: commit current CLI and spec changes", nil, "generic"},
		{"update files", "chore: update files", nil, "generic"},
		{"wip", "chore: wip", nil, "word"},
		{"misc cleanup", "chore: misc cleanup", nil, "word"},
		{"vague verb plus plural noun", "chore: update the files", nil, "generic"},
		{"address feedback", "fix: address review feedback", nil, "generic"},
		{"no type", "Add the thing to the other thing", nil, "type(scope)"},
		{"unknown type", "wibble: add the thing now", nil, "type(scope)"},
		{"too long", "feat: " + strings.Repeat("word ", 20), nil, "at most 72"},
		{"scope and breaking marker", "feat(api)!: rename tournament field to displayName", nil, ""},
		{"merge exempt", "Merge pull request #135 from wlambertz/x", nil, ""},
		{"fixup exempt", "fixup! feat: add deploy command", nil, ""},
		{"dependabot exempt", "chore(deps): bump spring-boot from 4.0.0 to 4.0.1", nil, ""},
		{"git comment lines ignored", "# Please enter the commit message\nfeat: add deploy command\n# Changes to be committed", nil, ""},
		{"empty", "\n# only comments\n", nil, "empty"},
		{"large diff needs body", "refactor(ro): split git helpers into focused files", large, "add a body"},
		{
			"large diff with body",
			"refactor(ro): split git helpers into focused files\n\nKeeps each file under one responsibility so tests stay small.",
			large, "",
		},
		{
			"trailer alone is not a body",
			"refactor(ro): split git helpers into focused files\n\nCo-authored-by: Claude Sonnet 5.5 <noreply@anthropic.com>",
			large, "add a body",
		},
		{"small diff needs no body", "refactor(ro): split git helpers into focused files", &diffStats{Files: 2, Lines: 30}, ""},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			problems := lintCommitMessage(tt.message, tt.stats, opts)
			if tt.wantFail == "" {
				if len(problems) != 0 {
					t.Fatalf("expected pass, got %v", problems)
				}
				return
			}
			if !strings.Contains(strings.Join(problems, "\n"), tt.wantFail) {
				t.Fatalf("expected a problem containing %q, got %v", tt.wantFail, problems)
			}
		})
	}
}

func TestLintOptionsExtraDenyPatterns(t *testing.T) {
	opts, err := lintOptionsFromConfig(config.CommitLint{ExtraDenyPatterns: []string{`(?i)^polish\b`}})
	if err != nil {
		t.Fatal(err)
	}
	if got := lintCommitMessage("chore: polish everything here", nil, opts); len(got) == 0 {
		t.Fatal("expected extra deny pattern to reject message")
	}
	if _, err := lintOptionsFromConfig(config.CommitLint{ExtraDenyPatterns: []string{"("}}); err == nil {
		t.Fatal("expected invalid regexp to be reported")
	}
}

func TestParseNumstat(t *testing.T) {
	got := parseNumstat("12\t3\ta.go\n-\t-\timg.png\n5\t0\tb.go\n")
	if got.Files != 3 || got.Lines != 20 {
		t.Fatalf("got %+v", got)
	}
}
