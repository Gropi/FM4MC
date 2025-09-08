#!/usr/bin/env bash
# Build the FM4MC Docker image and run a component or benchmarks, collecting results
set -e
IMAGE_NAME=fm4mc
MODE=${1:-jmh}
# Build image using repository root as context
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
if ! docker info >/dev/null 2>&1; then
  echo "Docker daemon not running. Attempting to start..." >&2
  if command -v systemctl >/dev/null 2>&1; then
    sudo systemctl start docker >/dev/null 2>&1 || true
  elif command -v service >/dev/null 2>&1; then
    sudo service docker start >/dev/null 2>&1 || true
  fi
  # Give the daemon a moment to initialize
  sleep 2
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker daemon still not running. Please start Docker and try again." >&2
  exit 1
fi
docker build -f "$SCRIPT_DIR/Dockerfile" -t "$IMAGE_NAME" "$ROOT_DIR"
mkdir -p "$SCRIPT_DIR/output"
docker run --rm -v "$SCRIPT_DIR/output":/output "$IMAGE_NAME" "$MODE"
echo "Results stored in Dockersetup/output"
