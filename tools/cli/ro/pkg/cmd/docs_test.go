package cmd

import (
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"testing"

	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
)

func gitIn(t *testing.T, dir string, args ...string) string {
	t.Helper()
	out, err := exec.Command("git", append([]string{"-C", dir}, args...)...).CombinedOutput()
	if err != nil {
		t.Fatalf("git %v: %v\n%s", args, err, out)
	}
	return string(out)
}

func TestCommitWikiLeavesOtherStagedFilesAlone(t *testing.T) {
	if _, err := exec.LookPath("git"); err != nil {
		t.Skip("git not available")
	}
	root := t.TempDir()
	gitIn(t, root, "init", "-q")
	gitIn(t, root, "config", "user.email", "test@example.com")
	gitIn(t, root, "config", "user.name", "test")
	gitIn(t, root, "config", "commit.gpgsign", "false")

	wikiFile := filepath.Join("docs", "wiki", "CLI.md")
	if err := os.MkdirAll(filepath.Join(root, "docs", "wiki"), 0o755); err != nil {
		t.Fatal(err)
	}
	for path, content := range map[string]string{wikiFile: "cli", "unrelated.txt": "x"} {
		if err := os.WriteFile(filepath.Join(root, path), []byte(content), 0o644); err != nil {
			t.Fatal(err)
		}
	}

	origCfg := cfg
	t.Cleanup(func() { cfg = origCfg })
	cfg = &config.Config{}
	cfg.Project.Root = root

	if err := stageWikiFile(wikiFile); err != nil {
		t.Fatalf("stageWikiFile: %v", err)
	}
	gitIn(t, root, "add", "unrelated.txt")
	if err := commitWiki("docs: update CLI reference"); err != nil {
		t.Fatalf("commitWiki: %v", err)
	}

	committed := gitIn(t, root, "show", "--name-only", "--format=", "HEAD")
	if strings.TrimSpace(committed) != "docs/wiki/CLI.md" {
		t.Fatalf("commit contained %q, want only docs/wiki/CLI.md", committed)
	}
	if staged := gitIn(t, root, "diff", "--cached", "--name-only"); strings.TrimSpace(staged) != "unrelated.txt" {
		t.Fatalf("staged after commit = %q, want unrelated.txt still staged", staged)
	}
}
