#!/usr/bin/env python3
"""Guard ordinary Bash issue creation while this worktree has a lifecycle session.

This is a workflow guard, not a shell sandbox. The lifecycle helper's subprocess
is not a separate Claude tool call, so it needs no environment-variable bypass.
"""
import json
import os
from pathlib import Path
import re
import shlex
import subprocess
import sys

VALUE_OPTIONS = {
    "--title", "-t", "--body", "-b", "--body-file", "-F", "--label", "-l",
    "--assignee", "-a", "--milestone", "-m", "--project", "-p", "--template", "-T",
    "--recover", "--hostname", "--repo", "-R",
}


def active_session(cwd):
    result = subprocess.run(
        ["git", "-C", cwd, "rev-parse", "--git-path", "rallyon-lifecycle.json"],
        capture_output=True, text=True, check=False,
    )
    if result.returncode:
        return None
    path = Path(result.stdout.strip())
    if not path.is_absolute():
        path = Path(cwd) / path
    if not path.exists():
        return None
    try:
        session = json.loads(path.read_text())
        if not isinstance(session, dict) or not isinstance(session.get("repository"), str):
            return {}
        return session
    except (ValueError, OSError):
        # An unreadable pointer is not evidence that the session ended.
        return {}


def repository(tokens):
    values = []
    skip = False
    for index, token in enumerate(tokens):
        if skip:
            skip = False
            continue
        if token in VALUE_OPTIONS - {"--repo", "-R"}:
            skip = True
            continue
        if token in ("-R", "--repo") and index + 1 < len(tokens):
            values.append(tokens[index + 1])
            skip = True
        elif token.startswith("--repo="):
            values.append(token.split("=", 1)[1])
        elif token.startswith("-R") and len(token) > 2:
            values.append(token[2:])
    if len(values) != 1:
        return None
    value = values[0].removeprefix("https://github.com/").rstrip("/")
    return value.lower() if re.fullmatch(r"[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+", value) else None


def creates_issue(tokens):
    commands = []
    skip = False
    for token in tokens:
        if skip:
            skip = False
            continue
        if token in VALUE_OPTIONS:
            skip = True
            continue
        if token.startswith("-"):
            continue
        commands.append(token)
        if len(commands) == 1 and token != "issue":
            return False
        if len(commands) == 2:
            return commands == ["issue", "create"] or commands == ["issue", "new"]
    return False


class ShellScanner:
    """Separate ordinary commands from literal input and executable expansions.

    This bounded scanner covers the shell forms supported by the workflow guard;
    it does not evaluate shell code or implement Bash's entire grammar.
    """

    def __init__(self, command, depth=0):
        if depth > 8:
            raise ValueError("Shell nesting exceeds inspection limit")
        self.command = command
        self.index = 0
        self.depth = depth
        self.commands = []

    def heredoc(self):
        start = self.index
        self.index += 2
        strip_tabs = self.command[self.index:self.index + 1] == "-"
        self.index += int(strip_tabs)
        while self.command[self.index:self.index + 1] in (" ", "\t"):
            self.index += 1
        word = self.index
        quote = None
        quoted = False
        while self.index < len(self.command):
            char = self.command[self.index]
            if char == "\\" and quote != "'":
                quoted = True
                self.index += 2
                continue
            if char in ("'", '"') and (quote is None or quote == char):
                quoted = True
                quote = char if quote is None else None
            elif quote is None and (char.isspace() or char in ";&|()<>"):
                break
            self.index += 1
        raw = self.command[word:self.index]
        tokens = shlex.split(raw, comments=False)
        if quote or len(tokens) != 1:
            raise ValueError("Cannot inspect heredoc delimiter")
        return self.command[start:self.index], (tokens[0], not quoted, strip_tabs)

    def heredoc_bodies(self, pending):
        for delimiter, expands, strip_tabs in pending:
            body = []
            while self.index < len(self.command):
                end = self.command.find("\n", self.index)
                end = len(self.command) if end < 0 else end + 1
                line = self.command[self.index:end]
                self.index = end
                candidate = line.lstrip("\t") if strip_tabs else line
                if candidate.removesuffix("\n") == delimiter:
                    break
                body.append(line)
            else:
                raise ValueError("Unterminated heredoc")
            if expands:
                # Quotes in an unquoted heredoc are data: only backslashes can
                # suppress expansions, unlike quotes in ordinary shell words.
                self.commands.extend(shell_fragments("".join(body), self.depth + 1, expansions_only=True))

    def backticks(self):
        self.index += 1
        start = self.index
        while self.index < len(self.command):
            char = self.command[self.index]
            if char == "\\":
                self.index += 2
                continue
            if char == "`":
                raw = self.command[start:self.index]
                self.index += 1
                raw = re.sub(r"\\([$`\\])", r"\1", raw)
                self.commands.extend(shell_fragments(raw, self.depth + 1))
                return
            self.index += 1
        raise ValueError("Unterminated backtick substitution")

    def scan(self, closing=False, expansions_only=False, depth=0):
        if depth + self.depth > 8:
            raise ValueError("Shell nesting exceeds inspection limit")
        output = []
        quote = None
        parentheses = 0
        pending = []
        while self.index < len(self.command):
            char = self.command[self.index]
            if char == "\\" and quote != "'":
                # Bash removes executable line continuations before tokenizing;
                # retaining them makes shlex invent a newline argument.
                if self.command[self.index + 1:self.index + 2] == "\n":
                    self.index += 2
                    continue
                output.append(self.command[self.index:self.index + 2])
                self.index += 2
                continue
            if not expansions_only and char in ("'", '"') and (quote is None or quote == char):
                quote = char if quote is None else None
                output.append(char)
                self.index += 1
                continue
            if quote != "'" and (self.command.startswith("$(", self.index) or
                                  (quote is None and not expansions_only and self.command[self.index:self.index + 2] in ("<(", ">("))):
                self.index += 2
                self.commands.append(self.scan(closing=True, depth=depth + 1))
                output.append("$RALLYON_SUBSTITUTION")
                continue
            if quote != "'" and char == "`":
                self.backticks()
                output.append("$RALLYON_SUBSTITUTION")
                continue
            if quote is None and not expansions_only:
                if char == "#" and (self.index == 0 or self.command[self.index - 1].isspace() or self.command[self.index - 1] in ";&|()"):
                    end = self.command.find("\n", self.index)
                    end = len(self.command) if end < 0 else end
                    output.append(self.command[self.index:end])
                    self.index = end
                    continue
                if self.command.startswith("<<<", self.index):
                    output.append("<<<")
                    self.index += 3
                    continue
                if self.command.startswith("<<", self.index):
                    header, document = self.heredoc()
                    output.append(header)
                    pending.append(document)
                    continue
                if char == "\n" and pending:
                    output.append("\n")
                    self.index += 1
                    self.heredoc_bodies(pending)
                    pending = []
                    continue
                if char == "(":
                    parentheses += 1
                elif char == ")":
                    if closing and not parentheses:
                        if pending:
                            raise ValueError("Heredoc body is missing")
                        self.index += 1
                        return "".join(output)
                    parentheses -= 1
            output.append(char)
            self.index += 1
        if closing or quote or pending:
            raise ValueError("Incomplete shell input")
        return "".join(output)


def shell_fragments(command, depth=0, expansions_only=False):
    scanner = ShellScanner(command, depth)
    outer = scanner.scan(expansions_only=expansions_only)
    return scanner.commands + ([] if expansions_only else [outer])


def blocked(command, target, depth=0):
    if depth > 8:
        return True
    try:
        fragments = shell_fragments(command, depth)
    except ValueError:
        return bool(re.search(r"\bgh\b.*\bissue\b.*\b(?:create|new)\b", command, re.S))
    return any(blocked_fragment(fragment, target, depth) for fragment in fragments)


def blocked_fragment(command, target, depth):
    try:
        lexer = shlex.shlex(command, posix=True, punctuation_chars=";&|()\n")
        lexer.whitespace = " \t\r"
        lexer.whitespace_split = True
        lexer.commenters = "#"
        tokens = list(lexer)
    except ValueError:
        return bool(re.search(r"\bgh\b.*\bissue\b.*\b(?:create|new)\b", command, re.S))
    segments = [[]]
    for token in tokens:
        if token and all(char in ";&|()\n" for char in token):
            segments.append([])
        else:
            segments[-1].append(token)
    for segment in segments:
        if not segment:
            continue
        index = 0
        # Quoted arguments to documentation commands do not execute.
        while index < len(segment) and re.match(r"^[A-Za-z_][A-Za-z_0-9]*=", segment[index]):
            index += 1
        executable = Path(segment[index]).name if index < len(segment) else ""
        if executable in ("echo", "printf", "cat"):
            continue
        for pos, token in enumerate(segment):
            executable = Path(token).name
            if executable in ("bash", "sh", "zsh", "dash", "eval"):
                if executable == "eval":
                    if blocked(" ".join(segment[pos + 1:]), target, depth + 1):
                        return True
                else:
                    for option in range(pos + 1, len(segment) - 1):
                        if segment[option].startswith("-") and "c" in segment[option]:
                            if blocked(segment[option + 1], target, depth + 1):
                                return True
            if executable == "gh":
                tail = segment[pos + 1:]
                if creates_issue(tail):
                    repo = repository(tail)
                    if not target or repo is None or repo == target.lower():
                        return True
    return False


def main():
    try:
        payload = json.load(sys.stdin)
    except ValueError:
        print("Cannot inspect lifecycle hook input.", file=sys.stderr)
        return 2
    project = os.environ.get("CLAUDE_PROJECT_DIR")
    session = active_session(project) if project else None
    if session is None:
        session = active_session(payload.get("cwd") or os.getcwd())
    if session is None:
        return 0
    command = payload.get("tool_input", {}).get("command", "")
    if blocked(command, session.get("repository", "")):
        print(
            "Blocked: an active feature lifecycle requires approval of the exact issue payload. "
            "Use python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py "
            "--change NAME create-issues, or explicitly deactivate the session for unrelated work.",
            file=sys.stderr,
        )
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
