#!/usr/bin/env python3
"""Run the Phase C local API scenarios without recording bearer credentials."""

from datetime import datetime, timezone
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import sys
import urllib.error
import urllib.request
import uuid


def main():
    root = Path(__file__).resolve().parents[4]
    helper = root / '.agents/skills/feature-lifecycle/scripts/lifecycle.py'
    spec = importlib.util.spec_from_file_location('lifecycle', helper)
    lifecycle = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(lifecycle)
    session = lifecycle.Session(root, 'retrieve-tournament-configuration')
    session.require('G7')

    base = os.environ.get('RALLYON_API_URL', 'http://localhost:8080').rstrip('/')
    token = subprocess.run(
        [os.environ.get('RO_BINARY', 'ro'), 'auth', 'token', '--format', 'raw'],
        cwd=root, text=True, capture_output=True, check=True, timeout=30,
    ).stdout.strip()
    if not token or '\n' in token:
        raise RuntimeError('Local token command did not return one raw token')

    report = dict(
        run_at=datetime.now(timezone.utc).isoformat(), api_base=base,
        source_index_fingerprint=session.fingerprint('G7'),
        authentication='Real local Keycloak token obtained through ro auth token',
        steps=[], human_g8_acceptance='pending',
    )
    output = Path(__file__).with_name('api-walkthrough-results.json')

    def save():
        output.write_text(json.dumps(report, indent=2) + '\n')

    def request(name, method, path, expected, body=None, version=None, auth='valid'):
        headers = {'Accept': 'application/json'}
        if auth == 'valid':
            headers['Authorization'] = 'Bearer ' + token
        elif auth == 'malformed':
            headers['Authorization'] = 'Bearer not-a-jwt'
        if version is not None:
            headers['If-Match'] = str(version)
        data = None
        if body is not None:
            headers['Content-Type'] = 'application/json'
            data = json.dumps(body).encode()
        message = urllib.request.Request(base + path, data=data, headers=headers, method=method)
        try:
            with urllib.request.urlopen(message, timeout=20) as response:
                status, raw = response.status, response.read()
        except urllib.error.HTTPError as response:
            status, raw = response.code, response.read()
        payload = json.loads(raw) if raw else None
        observed = {key: payload[key] for key in (
            'id', 'version', 'name', 'visibility', 'status', 'createdAt', 'createdByUserId',
            'lastModifiedAt', 'lastModifiedByUserId', 'title', 'detail',
        ) if isinstance(payload, dict) and key in payload}
        report['steps'].append(dict(name=name, method=method, path=path, expected=expected,
                                    actual=status, observations=observed))
        save()
        if status != expected:
            raise AssertionError(f'{name}: expected HTTP {expected}, received {status}')
        return payload

    name = 'Phase C pilot ' + uuid.uuid4().hex[:10]
    created = request('Create a partial PRIVATE draft', 'POST', '/api/tournamentmgmt/config/drafts?organizerId=42',
                      201, {'name': name, 'visibility': 'PRIVATE'})
    identifier = created['id']
    path = f'/api/tournamentmgmt/config/{identifier}'
    before = request('Retrieve the saved draft', 'GET', path, 200)
    assert before['id'] == identifier and before['name'] == name
    assert before['visibility'] == 'PRIVATE' and before['status'] == 'DRAFT'
    assert before['schedule'] is None and before['venue'] is None
    assert request('Repeat read without mutation', 'GET', path, 200) == before

    updated_name = name + ' updated'
    request('Commit a draft replacement', 'PUT', path + '/draft', 200,
            {'name': updated_name, 'visibility': 'PRIVATE', 'description': 'Phase C witnessed API pilot'},
            version=before['version'])
    after = request('Retrieve the committed replacement', 'GET', path, 200)
    assert after['name'] == updated_name and after['description'] == 'Phase C witnessed API pilot'
    assert after['version'] == before['version'] + 1
    assert after['createdAt'] == before['createdAt'] and after['createdByUserId'] == before['createdByUserId']
    assert after['lastModifiedByUserId'] == 42 and after['status'] == before['status']
    assert request('Repeat committed read without mutation', 'GET', path, 200) == after

    missing = request('Unknown numeric ID uses ProblemDetail', 'GET', '/api/tournamentmgmt/config/0', 404)
    assert missing['status'] == 404 and missing['title'] == 'Not Found'
    request('Anonymous access is rejected', 'GET', path, 401, auth='anonymous')
    request('Malformed bearer token is rejected', 'GET', path, 401, auth='malformed')
    session.require('G7')
    report.update(outcome='automated walkthrough passed', created_tournament_id=identifier,
                  data_checks='Partial draft, committed replacement and exact repeated snapshots verified',
                  cleanup='Pilot record retained only in the isolated rallyon-phase-c Compose database')
    save()
    print(json.dumps(report, indent=2))


if __name__ == '__main__':
    try:
        main()
    except Exception as error:
        print(f'Walkthrough failed: {type(error).__name__}: {error}', file=sys.stderr)
        sys.exit(1)
