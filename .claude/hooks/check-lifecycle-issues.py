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
        if token in ("--title", "-t", "--body", "-b", "--body-file", "-F", "--label", "-l", "--assignee", "-a", "--milestone", "-m", "--project", "-p", "--template", "-T", "--hostname"):
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


def expansion_text(command):
    """Mask literal single quotes and escaped characters before spotting execution."""
    output = []
    single = False
    double = False
    escaped = False
    for char in command:
        if escaped:
            output.append(" ")
            escaped = False
        elif char == "\\" and not single:
            escaped = True
            output.append(" ")
        elif char == "\'" and not double:
            single = not single
            output.append(" ")
        elif char == '"' and not single:
            double = not double
            output.append(char)
        else:
            output.append(" " if single else char)
    return "".join(output)


def blocked(command, target, depth=0):
    if depth > 8:
        return True
    # Substitutions execute even inside double-quoted documentation arguments.
    if re.search(r"(?:\$\(|`)[^\n]*(?:\bgh\b)[^\n]*\bissue\s+create\b", expansion_text(command)):
        return True
    try:
        lexer = shlex.shlex(command, posix=True, punctuation_chars=";&|()\n")
        lexer.whitespace = " \t\r"
        lexer.whitespace_split = True
        lexer.commenters = "#"
        tokens = list(lexer)
    except ValueError:
        return bool(re.search(r"\bgh\b.*\bissue\b.*\bcreate\b", command, re.S))
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
                # Search around global flags and wrappers without relying on
                # one fragile positional layout of the GitHub CLI command.
                for issue in range(len(tail) - 1):
                    if tail[issue] == "issue" and "create" in tail[issue + 1:]:
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
    cwd = payload.get("cwd") or os.environ.get("CLAUDE_PROJECT_DIR") or os.getcwd()
    session = active_session(cwd)
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
