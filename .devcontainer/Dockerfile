FROM golang:1.25.8-bookworm AS go-toolchain

FROM eclipse-temurin:25.0.2_10-jdk-jammy AS java-toolchain

FROM node:20.20.1-bookworm AS node-toolchain

FROM docker:27.5.1-cli AS docker-tooling

FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

COPY --from=go-toolchain /usr/local/go /usr/local/go
COPY --from=java-toolchain /opt/java/openjdk /opt/java/openjdk
COPY --from=node-toolchain /usr/local /usr/local
COPY --from=docker-tooling /usr/local/bin/docker /usr/local/bin/docker-real
COPY --from=docker-tooling /usr/local/libexec/docker /usr/local/libexec/docker

ENV JAVA_HOME=/opt/java/openjdk
ENV GOPATH=/home/vscode/go
ENV PATH=/usr/local/go/bin:/opt/java/openjdk/bin:/workspaces/rallyon/bin:${PATH}
ENV CHROME_BIN=/usr/bin/google-chrome
ENV PLAYWRIGHT_CHROME_BIN=/usr/bin/google-chrome
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        bash-completion \
        build-essential \
        ca-certificates \
        curl \
        git \
        gnupg \
        jq \
        less \
        postgresql-client \
        shellcheck \
        unzip \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
        | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg \
    && chmod a+r /etc/apt/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] https://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends google-chrome-stable \
    && npm install -g @openai/codex \
    && printf '%s\n' \
        '#!/usr/bin/env bash' \
        'set -euo pipefail' \
        'if [ "${EUID:-$(id -u)}" -eq 0 ]; then' \
        '  exec /usr/local/bin/docker-real "$@"' \
        'fi' \
        'exec sudo /usr/local/bin/docker-real "$@"' \
        > /usr/local/bin/docker \
    && chmod +x /usr/local/bin/docker \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
