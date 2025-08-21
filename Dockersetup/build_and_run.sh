#!/bin/bash
# Build the FM4MC Docker image and run benchmarks, collecting results
set -e
IMAGE_NAME=fm4mc
# Build image using repository root as context
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
docker build -f "$SCRIPT_DIR/Dockerfile" -t "$IMAGE_NAME" "$ROOT_DIR"
mkdir -p "$SCRIPT_DIR/output"
docker run --rm -v "$SCRIPT_DIR/output":/output "$IMAGE_NAME"
echo "Benchmark results stored in Dockersetup/output/benchmark.csv"
