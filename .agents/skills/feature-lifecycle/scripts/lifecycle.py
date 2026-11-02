#!/usr/bin/env python3
"""Local lifecycle evidence and approved, resumable GitHub issue creation."""

import argparse
from datetime import datetime, timezone
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import tempfile

GATES = ['G1', 'G2', 'G3', 'G4', 'G5', 'G6', 'G7', 'G8', 'merge']
HUMAN = {'G1', 'G2', 'G4', 'G8', 'merge'}
LENSES = ['architecture', 'correctness', 'tests']


class LifecycleError(Exception):
    pass


def git(root, *args):
    return subprocess.check_output(['git', '-C', str(root), *args], stderr=subprocess.PIPE)


def digest(value):
    return hashlib.sha256(value).hexdigest()


def atomic_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    fd, temp = tempfile.mkstemp(dir=path.parent)
    try:
        with os.fdopen(fd, 'w') as stream:
            json.dump(data, stream, indent=2)
            stream.write('\n')
        os.replace(temp, path)
    finally:
        if os.path.exists(temp):
            os.unlink(temp)


class Session:
    def __init__(self, root, name):
        self.root = Path(root).resolve()
        if not re.fullmatch(r'[a-z0-9]+(?:-[a-z0-9]+)*', name):
            raise LifecycleError('Use a kebab-case change name')
        self.change = self.root / 'openspec' / 'changes' / name
        if not self.change.is_dir() or self.change.is_symlink():
            raise LifecycleError('Create the OpenSpec change first')
        if not self.change.resolve().is_relative_to(self.root):
            raise LifecycleError('Change must be inside this repository')
        self.path = self.change / '.lifecycle.json'
        pointer = Path(git(self.root, 'rev-parse', '--git-path', 'rallyon-lifecycle.json').decode().strip())
        self.pointer = pointer if pointer.is_absolute() else self.root / pointer
        self.state = json.loads(self.path.read_text()) if self.path.exists() else None
        if self.state and self.state.get('version') != 1:
            raise LifecycleError('Unsupported lifecycle record version')

    def save(self):
        atomic_json(self.path, self.state)

    def active(self):
        if not self.state or not self.pointer.exists():
            raise LifecycleError('No active session; run start to resume')
        pointer = json.loads(self.pointer.read_text())
        if pointer.get('change') != self.change.relative_to(self.root).as_posix():
            raise LifecycleError('Another change is active')
        if pointer.get('repository') != self.state['repository']:
            raise LifecycleError('Active pointer repository does not match the session')

    def start(self, repository, maker, mode='full', additional_makers=()):
        if not re.fullmatch(r'[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+', repository) or not maker.strip():
            raise LifecycleError('Provide repository owner/name and a maker identity')
        if self.pointer.exists():
            pointer = json.loads(self.pointer.read_text())
            if pointer['change'] != self.change.relative_to(self.root).as_posix():
                raise LifecycleError('Deactivate the other change first')
        if self.state:
            if (repository, maker, mode) != (self.state['repository'], self.state['maker'], self.state['mode']):
                raise LifecycleError('Cannot change identity, repository or mode on resume')
        else:
            self.state = dict(version=1, repository=repository, maker=maker, makers=[maker], mode=mode,
                              records=[], issues={}, attempts={})
        self.state['makers'] = sorted(set(self.state['makers']) | set(additional_makers))
        if not all(isinstance(actor, str) and actor.strip() for actor in self.state['makers']):
            raise LifecycleError('Maker identities must be nonempty')
        self.save()
        atomic_json(self.pointer, dict(change=self.change.relative_to(self.root).as_posix(), repository=repository))

    def file_hash(self, path):
        if not path.resolve().is_relative_to(self.root) or path.is_symlink():
            raise LifecycleError('Artifacts must be ordinary files inside this repository')
        return digest(path.read_bytes())

    def manifest(self, gate):
        if gate in {'G1', 'G2', 'G3', 'G4'}:
            names = ['lifecycle.md'] if self.state['mode'] == 'reduced' else ['refinement.md']
            if gate != 'G1':
                planning = ['proposal.md', 'design.md', 'value.md']
                names += [name for name in planning if self.state['mode'] == 'full'
                          or (self.change / name).exists() or (self.change / name).is_symlink()]
                specs = sorted(self.change.glob('specs/**/spec.md'))
                if not specs and self.state['mode'] == 'full':
                    raise LifecycleError('Specification scenarios are missing')
                names += [p.relative_to(self.change).as_posix() for p in specs]
            if gate == 'G4':
                names += ['tasks.md', 'issue-drafts.json']
            manifest = {str((self.change / n).relative_to(self.root)): self.file_hash(self.change / n) for n in names}
            if gate == 'G4':
                # OpenSpec updates completion boxes during apply; approved task
                # wording and ordering remain bound to G4, completion does not.
                tasks = (self.change / 'tasks.md').read_text()
                tasks = re.sub(r'(?m)^(\s*-\s+)\[[ xX]\](?=\s)', r'\1[ ]', tasks)
                manifest[str((self.change / 'tasks.md').relative_to(self.root))] = digest(tasks.encode())
            return manifest
        paths = git(self.root, 'ls-files', '-z', '--cached', '--others', '--exclude-standard').decode().split('\0')
        # Reports cannot participate in the revision they certify. Their own hashes
        # are validated separately, including any later edits to those reports.
        excluded = {self.path.relative_to(self.root).as_posix()}
        excluded.update((self.change / name).relative_to(self.root).as_posix()
                        for name in ['verification.md', 'review.md', 'uat.md'])
        evidence_dir = (self.change / 'evidence').relative_to(self.root).as_posix() + '/'
        manifest = {}
        for name in sorted(set(paths) - excluded - {''}):
            if name.startswith(evidence_dir):
                continue
            path = self.root / name
            manifest[name] = ('symlink:' + digest(os.readlink(path).encode()) if path.is_symlink()
                              else self.file_hash(path) if path.exists() else 'deleted')
        for entry in git(self.root, 'ls-files', '--stage', '-z').decode().split('\0'):
            if entry:
                metadata, name = entry.split('\t', 1)
                if name not in excluded and not name.startswith(evidence_dir):
                    manifest['index:' + name] = metadata
        if gate in {'G8', 'merge'}:
            manifest['uat'] = self.file_hash(self.change / 'uat.md')
        return manifest

    def fingerprint(self, gate):
        return digest(json.dumps(self.manifest(gate), sort_keys=True).encode())

    def latest(self, gate, lens=None):
        return next((r for r in reversed(self.state['records'])
                     if r['gate'] == gate and r.get('lens') == lens), None)

    def dependencies(self, gate):
        result = {}
        for previous in GATES[:GATES.index(gate)]:
            lenses = LENSES if previous == 'G7' and self.state['mode'] == 'full' else [None]
            for lens in lenses:
                record = self.latest(previous, lens)
                result[previous + ':' + str(lens)] = digest(json.dumps(record, sort_keys=True).encode())
        return result

    def require(self, gate):
        if GATES.index(gate) >= GATES.index('G5'):
            self.require_resolved_attempts()
        for previous in GATES[:GATES.index(gate) + 1]:
            lenses = LENSES if previous == 'G7' and self.state['mode'] == 'full' else [None]
            for lens in lenses:
                r = self.latest(previous, lens)
                if not r or r['outcome'] != 'pass':
                    raise LifecycleError(f'{previous} {lens or ""} pending or rejected')
                if previous not in HUMAN and r['actor'] in self.state['makers']:
                    raise LifecycleError(f'{previous} checker is an implementation maker')
                if r['fingerprint'] != self.fingerprint(previous):
                    raise LifecycleError(f'{previous} evidence is stale')
                if r.get('dependencies') != self.dependencies(previous):
                    raise LifecycleError(f'{previous} preceding gate decision changed')
                if r.get('evidence') and r['evidence_hash'] != self.file_hash(self.root / r['evidence']):
                    raise LifecycleError(f'{previous} checker report changed')

    def record(self, gate, actor, outcome='pass', quote=None, source=None, evidence=None, lens=None):
        self.active()
        index = GATES.index(gate)
        if index:
            self.require(GATES[index - 1])
        if gate in HUMAN:
            if not quote or not source or not actor.strip():
                raise LifecycleError('Human approval needs an explicit quote, actor and source reference')
        else:
            if not actor or actor in self.state['makers']:
                raise LifecycleError('An independent checker identity is required')
            if not evidence:
                raise LifecycleError('Checker evidence is required')
        if gate == 'G7' and self.state['mode'] == 'full' and lens not in LENSES:
            raise LifecycleError('Full review requires a named lens')
        if lens and (gate != 'G7' or self.state['mode'] == 'reduced'):
            raise LifecycleError('Named review lenses apply only to full G7')
        if gate == 'G4':
            self.drafts()
        if gate == 'G5':
            self.require_resolved_attempts()
            for item in self.drafts()['issues']:
                if item['id'] not in self.state['issues'] or self.state['attempts'][item['id']]['payload'] != item:
                    raise LifecycleError('Create or reconcile every current approved payload before G5')
        record = dict(gate=gate, actor=actor, outcome=outcome, quote=quote, source=source,
                      lens=lens, timestamp=datetime.now(timezone.utc).isoformat())
        if evidence:
            path = (self.root / evidence).resolve()
            allowed = path.is_relative_to(self.change / 'evidence') or path in [self.change / n for n in ['verification.md', 'review.md', 'uat.md']]
            if not allowed:
                raise LifecycleError('Put checker reports in change/evidence/ or verification.md, review.md, uat.md')
            record.update(evidence=path.relative_to(self.root).as_posix(), evidence_hash=self.file_hash(path))
        record['fingerprint'] = self.fingerprint(gate)
        record['manifest'] = self.manifest(gate)
        record['dependencies'] = self.dependencies(gate)
        self.state['records'].append(record)
        self.save()

    def drafts(self):
        data = json.loads((self.change / 'issue-drafts.json').read_text())
        if set(data) != {'repository', 'issues'} or data['repository'] != self.state['repository']:
            raise LifecycleError('Issue payload repository must match the session')
        if not isinstance(data['issues'], list) or not data['issues']:
            raise LifecycleError('At least one issue draft is required')
        ids = set()
        for item in data['issues']:
            if set(item) - {'id', 'title', 'body', 'depends_on', 'labels', 'assignees', 'milestone'}:
                raise LifecycleError('Unknown issue payload fields')
            if not all(isinstance(item.get(k), str) and item[k].strip() for k in ['id', 'title', 'body']):
                raise LifecycleError('Each draft needs nonempty id, title and body')
            for key in ['labels', 'assignees', 'depends_on']:
                if not isinstance(item.get(key, []), list) or not all(isinstance(v, str) for v in item.get(key, [])):
                    raise LifecycleError(f'{key} must be a string array')
            if item['id'] in ids or not set(item.get('depends_on', [])).issubset(ids):
                raise LifecycleError('Draft IDs must be unique and dependencies ordered before dependents')
            ids.add(item['id'])
            if 'milestone' in item and not isinstance(item['milestone'], str):
                raise LifecycleError('milestone must be a title string')
        return data

    def unresolved_attempts(self):
        return {key: attempt for key, attempt in self.state['attempts'].items()
                if key not in self.state['issues']}

    def require_resolved_attempts(self):
        pending = self.unresolved_attempts()
        if pending:
            raise LifecycleError(f'Ambiguous prior creation for {", ".join(sorted(pending))}; reconcile before continuing')

    def create_issues(self):
        self.active()
        self.require('G4')
        self.require_resolved_attempts()
        data = self.drafts()
        for item in data['issues']:
            previous = self.state['attempts'].get(item['id'])
            if previous and previous['payload'] != item:
                raise LifecycleError('An attempted draft changed; restore it or use a new draft ID')
        for item in data['issues']:
            key = item['id']
            if key in self.state['issues']:
                continue
            self.state['attempts'][key] = dict(payload=item, timestamp=datetime.now(timezone.utc).isoformat())
            self.save()
            with tempfile.NamedTemporaryFile(mode='w', suffix='.md') as body:
                body.write(item['body'])
                body.flush()
                cmd = ['gh', 'issue', 'create', '--repo', data['repository'], '--title', item['title'], '--body-file', body.name]
                for field, flag in [('labels', '--label'), ('assignees', '--assignee')]:
                    for value in item.get(field, []):
                        cmd += [flag, value]
                if item.get('milestone'):
                    cmd += ['--milestone', item['milestone']]
                try:
                    result = subprocess.run(cmd, text=True, capture_output=True, check=False, timeout=60)
                except (OSError, subprocess.TimeoutExpired) as error:
                    raise LifecycleError(f'Creation outcome for {key} is ambiguous; reconcile before retrying') from error
            url = result.stdout.strip()
            if result.returncode or not re.fullmatch(r'https://github.com/' + re.escape(data['repository']) + r'/issues/\d+', url):
                raise LifecycleError(f'Creation outcome for {key} is ambiguous; inspect GitHub and reconcile')
            self.state['issues'][key] = url
            self.save()
        return self.state['issues']

    def reconcile(self, key, url, retry, source):
        self.active()
        if key not in self.state['attempts'] or key in self.state['issues'] or not source:
            raise LifecycleError('Reconciliation needs an unresolved attempt and verification source')
        # Recovery records the remote outcome of the saved request, even when
        # later scope edits made G4 stale or removed that draft entirely.
        attempt = self.state['attempts'][key]
        if retry and not url:
            self.state['attempts'].pop(key)
        elif not retry and url and re.fullmatch(r'https://github.com/' + re.escape(self.state['repository']) + r'/issues/\d+', url):
            self.state['issues'][key] = url
        else:
            raise LifecycleError('Provide a verified issue URL or --retry')
        self.state.setdefault('reconciliations', []).append(dict(
            id=key, url=url, retry=retry, source=source, attempt=attempt,
            timestamp=datetime.now(timezone.utc).isoformat()))
        self.save()

    def status(self):
        result = {}
        for gate in GATES:
            try:
                self.require(gate)
                result[gate] = 'passed'
            except (LifecycleError, OSError) as error:
                result[gate] = str(error)
        snapshots = {}
        for gate in ['G1', 'G2', 'G4']:
            try:
                snapshots[gate] = dict(hash=self.fingerprint(gate), manifest=self.manifest(gate))
            except (LifecycleError, OSError) as error:
                snapshots[gate] = dict(pending=str(error))
        return dict(mode=self.state['mode'], makers=self.state['makers'], gates=result,
                    snapshots=snapshots, issues=self.state['issues'], unresolved_attempts=self.unresolved_attempts())


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--change', required=True)
    commands = parser.add_subparsers(dest='command', required=True)
    start = commands.add_parser('start')
    start.add_argument('--repository', required=True)
    start.add_argument('--maker', required=True)
    start.add_argument('--additional-maker', action='append', default=[])
    start.add_argument('--mode', choices=['full', 'reduced'], default='full')
    commands.add_parser('status')
    commands.add_parser('deactivate')
    approve = commands.add_parser('approve')
    approve.add_argument('gate', choices=sorted(HUMAN))
    approve.add_argument('--actor', required=True)
    approve.add_argument('--quote', required=True)
    approve.add_argument('--source', required=True)
    approve.add_argument('--outcome', choices=['pass', 'fail'], default='pass')
    check = commands.add_parser('check')
    check.add_argument('gate', choices=GATES)
    check.add_argument('--require', action='store_true')
    check.add_argument('--checker')
    check.add_argument('--evidence')
    check.add_argument('--outcome', choices=['pass', 'fail'], default='pass')
    check.add_argument('--lens', choices=LENSES)
    create = commands.add_parser('create-issues')
    create.add_argument('--reconcile')
    create.add_argument('--url')
    create.add_argument('--retry', action='store_true')
    create.add_argument('--source')
    args = parser.parse_args()
    acquired_lock = None
    try:
        root = git(Path.cwd(), 'rev-parse', '--show-toplevel').decode().strip()
        lock = Path(git(root, 'rev-parse', '--git-path', 'rallyon-lifecycle.lock').decode().strip())
        if not lock.is_absolute():
            lock = Path(root) / lock
        # Concurrent helpers must not submit the same approved issue twice.
        try:
            with lock.open('x') as stream:
                json.dump(dict(pid=os.getpid(), change=args.change, command=args.command), stream)
            acquired_lock = lock
        except FileExistsError as error:
            raise LifecycleError('Another helper holds rallyon-lifecycle.lock; verify that process has ended before removing a stale lock') from error
        session = Session(root, args.change)
        if args.command == 'start':
            session.start(args.repository, args.maker, args.mode, args.additional_maker)
        else:
            session.active()
            if args.command == 'status':
                print(json.dumps(session.status(), indent=2))
            elif args.command == 'deactivate':
                session.pointer.unlink()
            elif args.command == 'approve':
                session.record(args.gate, args.actor, args.outcome, args.quote, args.source)
            elif args.command == 'check':
                if args.require:
                    session.require(args.gate)
                elif args.gate in HUMAN or not args.checker:
                    raise LifecycleError('Use approve for human gates, or supply --checker')
                else:
                    session.record(args.gate, args.checker, args.outcome, evidence=args.evidence, lens=args.lens)
            elif args.reconcile:
                session.reconcile(args.reconcile, args.url, args.retry, args.source)
            elif args.url or args.retry or args.source:
                raise LifecycleError('Recovery flags require --reconcile ID')
            else:
                print(json.dumps(session.create_issues(), indent=2))
    except (LifecycleError, OSError, ValueError, KeyError, TypeError, subprocess.CalledProcessError) as error:
        print(f'Lifecycle blocked: {error}', file=sys.stderr)
        return 1
    finally:
        if acquired_lock:
            acquired_lock.unlink(missing_ok=True)
    return 0


if __name__ == '__main__':
    sys.exit(main())
