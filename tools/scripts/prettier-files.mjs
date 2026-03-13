import { spawnSync } from "node:child_process"
import { createRequire } from "node:module"

const require = createRequire(import.meta.url)

const [action, scope, ...rawOptions] = process.argv.slice(2)

if (!["check", "write"].includes(action) || !["changed", "tracked"].includes(scope)) {
  console.error(
    "Usage: node tools/scripts/prettier-files.mjs <check|write> <changed|tracked> [--base=<rev>] [--head=<rev>]"
  )
  process.exit(1)
}

const options = parseOptions(rawOptions)
const repoRoot = gitOutput(["rev-parse", "--show-toplevel"]).trim()
const prettierBin = require.resolve("prettier/bin/prettier.cjs")

process.chdir(repoRoot)

const files = scope === "tracked" ? trackedFiles() : changedFiles(options)

if (files.length === 0) {
  console.log(`No ${scope} tracked files to ${action}.`)
  process.exit(0)
}

console.log(`Running Prettier ${action} on ${files.length} ${scope} file${files.length === 1 ? "" : "s"}...`)
process.exit(runPrettier(files, action, prettierBin))

function parseOptions(args) {
  const options = {
    base: undefined,
    head: undefined,
  }

  for (let index = 0; index < args.length; index += 1) {
    const argument = args[index]

    if (argument.startsWith("--base=")) {
      options.base = argument.slice("--base=".length)
      continue
    }

    if (argument === "--base") {
      options.base = args[index + 1]
      index += 1
      continue
    }

    if (argument.startsWith("--head=")) {
      options.head = argument.slice("--head=".length)
      continue
    }

    if (argument === "--head") {
      options.head = args[index + 1]
      index += 1
      continue
    }

    console.error(`Unknown option: ${argument}`)
    process.exit(1)
  }

  return options
}

function trackedFiles() {
  return gitFileList(["ls-files", "-z"])
}

function changedFiles({ base, head }) {
  const files = new Set()

  if (base && head) {
    addDiffFiles(files, [base, head])
  } else {
    const defaultBranch = upstreamDefaultBranch()

    if (defaultBranch) {
      const mergeBase = gitOutput(["merge-base", "HEAD", defaultBranch], { allowFailure: true }).trim()

      if (mergeBase) {
        addDiffFiles(files, [mergeBase, "HEAD"])
      }
    }
  }

  addDiffFiles(files, ["--cached"])
  addDiffFiles(files, [])
  addFileList(files, gitFileList(["ls-files", "--others", "--exclude-standard", "-z"], { allowFailure: true }))

  return Array.from(files).sort()
}

function upstreamDefaultBranch() {
  return gitOutput(["symbolic-ref", "--quiet", "--short", "refs/remotes/origin/HEAD"], {
    allowFailure: true,
  }).trim()
}

function addDiffFiles(files, args) {
  addFileList(
    files,
    gitFileList(["diff", "--name-only", "--diff-filter=ACMRTUXB", "-z", ...args], {
      allowFailure: true,
    })
  )
}

function addFileList(files, candidates) {
  for (const candidate of candidates) {
    if (!candidate || candidate === "wiki" || candidate.startsWith("wiki/")) {
      continue
    }
    files.add(candidate)
  }
}

function gitFileList(args, options = {}) {
  return splitNullSeparated(gitOutput(args, options))
}

function gitOutput(args, { allowFailure = false } = {}) {
  const result = spawnSync("git", args, {
    encoding: "utf8",
  })

  if (result.status !== 0 && !allowFailure) {
    process.stderr.write(result.stderr)
    process.exit(result.status ?? 1)
  }

  return result.stdout ?? ""
}

function splitNullSeparated(value) {
  return value.split("\0").filter(Boolean)
}

function runPrettier(files, prettierAction, prettierBin) {
  let exitCode = 0

  for (const chunk of chunkFiles(files, 100)) {
    const result = spawnSync(process.execPath, [prettierBin, `--${prettierAction}`, "--ignore-unknown", ...chunk], {
      stdio: "inherit",
    })

    if (result.status && result.status !== 0) {
      exitCode = result.status

      if (prettierAction === "write") {
        break
      }
    }
  }

  return exitCode
}

function chunkFiles(files, chunkSize) {
  const chunks = []

  for (let index = 0; index < files.length; index += chunkSize) {
    chunks.push(files.slice(index, index + chunkSize))
  }

  return chunks
}
