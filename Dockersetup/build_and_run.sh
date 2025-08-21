#!/usr/bin/env bash
# Build the FM4MC Docker image and run benchmarks, collecting results
set -e
IMAGE_NAME=fm4mc
# Build image using repository root as context
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
if ! docker info >/dev/null 2>&1; then
  echo "Docker daemon not running. Please start Docker and try again." >&2
  exit 1
fi
docker build -f "$SCRIPT_DIR/Dockerfile" -t "$IMAGE_NAME" "$ROOT_DIR"
mkdir -p "$SCRIPT_DIR/output"
docker run --rm -v "$SCRIPT_DIR/output":/output "$IMAGE_NAME"
echo "Benchmark results stored in Dockersetup/output/benchmark.csv"
