package config

import (
	"errors"
	"fmt"
	"os"
	"path/filepath"
	"strings"

	"github.com/spf13/viper"
)

type Paths struct {
	ServiceRoot    string `mapstructure:"serviceRoot"`
	AppRoot        string `mapstructure:"appRoot"`
	ThirdPartyRoot string `mapstructure:"thirdPartyRoot"`
	AdminRoot      string `mapstructure:"adminRoot"`
}

type App struct {
	Path    string              `mapstructure:"path"`
	Scripts map[string][]string `mapstructure:"scripts"`
}

type Build struct {
	MavenWrapper string   `mapstructure:"mavenWrapper"`
	DefaultGoals []string `mapstructure:"defaultGoals"`
}

type Docker struct {
	Workflow    string `mapstructure:"workflow"`
	ImageRepo   string `mapstructure:"imageRepo"`
	Registry    string `mapstructure:"registry"`
	Context     string `mapstructure:"context"`
	Dockerfile  string `mapstructure:"dockerfile"`
	ComposeFile string `mapstructure:"composeFile"`
}

type Deploy struct {
	Repo             string            `mapstructure:"repo"`
	Workflow         string            `mapstructure:"workflow"`
	DefaultRef       string            `mapstructure:"defaultRef"`
	EnvRefs          map[string]string `mapstructure:"envRefs"`
	RequireClean     bool              `mapstructure:"requireClean"`
	RequireProtected bool              `mapstructure:"requireProtected"`
	RequireGreen     bool              `mapstructure:"requireGreen"`
	PollInterval     string            `mapstructure:"pollInterval"`
	PollTimeout      string            `mapstructure:"pollTimeout"`
	Inputs           map[string]string `mapstructure:"inputs"`
	DefaultWait      bool              `mapstructure:"defaultWait"`
}

type Git struct {
	ConventionalCommits bool   `mapstructure:"conventionalCommits"`
	DefaultRemote       string `mapstructure:"defaultRemote"`
	DefaultBranch       string `mapstructure:"defaultBranch"`
}

type Output struct {
	JSON    bool `mapstructure:"json"`
	Verbose bool `mapstructure:"verbose"`
	Quiet   bool `mapstructure:"quiet"`
}

type Docs struct {
	Output            string `mapstructure:"output"`
	WikiOutput        string `mapstructure:"wikiOutput"`
	PublishToWiki     bool   `mapstructure:"publishToWiki"`
	AutoStageWiki     bool   `mapstructure:"autoStageWiki"`
	WikiCommitMessage string `mapstructure:"wikiCommitMessage"`
}

type Scaffold struct {
	TemplateDir    string `mapstructure:"templateDir"`
	DefaultPackage string `mapstructure:"defaultPackage"`
	BasePath       string `mapstructure:"basePath"`
}

type Telemetry struct {
	Enabled         bool     `mapstructure:"enabled"`
	Endpoint        string   `mapstructure:"endpoint"`
	ClientID        string   `mapstructure:"clientId"`
	CollectCommands []string `mapstructure:"collectCommands"`
}

type Project struct {
	Name string `mapstructure:"name"`
	Root string `mapstructure:"root"`
}

type Config struct {
	Project   Project        `mapstructure:"project"`
	Paths     Paths          `mapstructure:"paths"`
	Build     Build          `mapstructure:"build"`
	Docker    Docker         `mapstructure:"docker"`
	Deploy    Deploy         `mapstructure:"deploy"`
	Git       Git            `mapstructure:"git"`
	Output    Output         `mapstructure:"output"`
	Docs      Docs           `mapstructure:"docs"`
	Scaffold  Scaffold       `mapstructure:"scaffold"`
	Telemetry Telemetry      `mapstructure:"telemetry"`
	Apps      map[string]App `mapstructure:"apps"`
}

// Load merges configuration from ro.yaml, environment, and provides accessors.
func Load(repoRoot string) (*Config, error) {
	v := viper.New()
	v.SetEnvPrefix("RO")
	v.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))
	v.AutomaticEnv()

	// Defaults
	v.SetDefault("project.name", "rallyon")
	v.SetDefault("project.root", ".")
	v.SetDefault("paths.serviceRoot", "service/tournamentmgmt")
	v.SetDefault("paths.appRoot", "application")
	v.SetDefault("paths.thirdPartyRoot", "3rd_party")
	v.SetDefault("paths.adminRoot", "admin")
	v.SetDefault("build.mavenWrapper", "./mvnw")
	v.SetDefault("build.defaultGoals", []string{"clean", "verify"})
	v.SetDefault("docker.workflow", ".github/workflows/Tournamentmgmt-docker.yaml")
	v.SetDefault("docker.imageRepo", "wlambertz/tournamentmgmt")
	v.SetDefault("docker.registry", "ghcr.io")
	v.SetDefault("docker.context", "service/tournamentmgmt")
	v.SetDefault("docker.dockerfile", "service/tournamentmgmt/Dockerfile")
	v.SetDefault("docker.composeFile", "docker-compose.yml")
	v.SetDefault("deploy.repo", "wlambertz/rallyon")
	v.SetDefault("deploy.workflow", ".github/workflows/Tournamentmgmt-deploy.yaml")
	v.SetDefault("deploy.defaultRef", "main")
	v.SetDefault("deploy.envRefs", map[string]string{"prod": "main"})
	v.SetDefault("deploy.requireClean", true)
	v.SetDefault("deploy.requireProtected", true)
	v.SetDefault("deploy.requireGreen", true)
	v.SetDefault("deploy.pollInterval", "15s")
	v.SetDefault("deploy.pollTimeout", "10m")
	v.SetDefault("deploy.inputs", map[string]string{})
	v.SetDefault("deploy.defaultWait", true)
	v.SetDefault("git.conventionalCommits", true)
	v.SetDefault("git.defaultRemote", "origin")
	v.SetDefault("git.defaultBranch", "main")
	v.SetDefault("output.json", false)
	v.SetDefault("output.verbose", false)
	v.SetDefault("output.quiet", false)
	v.SetDefault("docs.output", "docs/cli-reference.md")
	v.SetDefault("docs.wikiOutput", "docs/wiki/CLI.md")
	v.SetDefault("docs.publishToWiki", false)
	v.SetDefault("docs.autoStageWiki", false)
	v.SetDefault("docs.wikiCommitMessage", "docs: update CLI reference")
	v.SetDefault("scaffold.templateDir", "tools/cli/ro/templates")
	v.SetDefault("scaffold.defaultPackage", "com.rallyon.tournament")
	v.SetDefault("scaffold.basePath", "service/tournamentmgmt/src")
	v.SetDefault("telemetry.enabled", false)
	v.SetDefault("telemetry.endpoint", "")
	v.SetDefault("telemetry.clientId", "")
	v.SetDefault("telemetry.collectCommands", []string{"deploy", "docker build", "docker compose", "scaffold module"})
	v.SetDefault("apps", map[string]App{})

	// Config file from repo root if provided, else perform upward search from CWD
	if repoRoot == "" {
		start, _ := os.Getwd()
		repoRoot = findRepoRoot(start)
	}
	if repoRoot != "" {
		v.SetConfigName("ro")
		v.SetConfigType("yaml")
		v.AddConfigPath(repoRoot)
		_ = v.ReadInConfig() // ignore if missing; env/defaults still apply
	}

	var cfg Config
	if err := v.Unmarshal(&cfg); err != nil {
		return nil, err
	}
	if cfg.Apps == nil {
		cfg.Apps = map[string]App{}
	}

	// Normalize to absolute paths
	if repoRoot == "" {
		repoRoot, _ = os.Getwd()
	}
	toAbs := func(p string) string {
		if p == "" {
			return p
		}
		if filepath.IsAbs(p) {
			return p
		}
		return filepath.Clean(filepath.Join(repoRoot, p))
	}
	cfg.Project.Root = toAbs(cfg.Project.Root)
	cfg.Paths.ServiceRoot = toAbs(cfg.Paths.ServiceRoot)
	cfg.Paths.AppRoot = toAbs(cfg.Paths.AppRoot)
	cfg.Paths.ThirdPartyRoot = toAbs(cfg.Paths.ThirdPartyRoot)
	cfg.Paths.AdminRoot = toAbs(cfg.Paths.AdminRoot)
	for name, app := range cfg.Apps {
		app.Path = toAbs(app.Path)
		cfg.Apps[name] = app
	}

	// Platform-specific maven wrapper normalization
	cfg.Build.MavenWrapper = normalizeMavenWrapper(cfg.Build.MavenWrapper)

	// Validate basics
	if _, err := os.Stat(cfg.Paths.ServiceRoot); err != nil {
		return nil, fmt.Errorf("paths.serviceRoot does not exist: %w", err)
	}
	if cfg.Build.MavenWrapper == "" {
		return nil, errors.New("build.mavenWrapper is required")
	}

	return &cfg, nil
}

func findRepoRoot(start string) string {
	dir := start
	for {
		if dir == "" || dir == "/" || dir == "\\" {
			return ""
		}
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

func normalizeMavenWrapper(value string) string {
	if value == "" {
		return value
	}
	// On Windows prefer mvnw.cmd if wrapper path points to repo root
	if os.PathSeparator == '\\' {
		base := filepath.Base(value)
		if base == "./mvnw" || base == "mvnw" {
			return "mvnw.cmd"
		}
	}
	return value
}

// MavenExecutablePath returns an absolute path to the wrapper inside the service root.
func MavenExecutablePath(serviceRoot string, wrapper string) string {
	// If wrapper already has a path separator or is absolute, join/clean accordingly
	if wrapper == "" {
		wrapper = "mvnw"
		if os.PathSeparator == '\\' {
			wrapper = "mvnw.cmd"
		}
	}
	if filepath.IsAbs(wrapper) {
		return wrapper
	}
	// Ensure we point to the wrapper located in the service root
	return filepath.Clean(filepath.Join(serviceRoot, wrapper))
}
