#!/usr/bin/env bash
set -euo pipefail

# Fast end-to-end check:
# - small number of iterations
# - restrict benchmarks if desired

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/Dockersetup/output/smoke"
mkdir -p "${OUT_DIR}"

export THREADS="${THREADS:-1}"
export WARMUP_ITERS="${WARMUP_ITERS:-1}"
export MEAS_ITERS="${MEAS_ITERS:-1}"
export FORKS="${FORKS:-1}"

# Restrict to a small subset by default (adjust this regex to a known-fast benchmark)
export BENCH_REGEX="${BENCH_REGEX:-.*}"

export OUT_CSV="${OUT_DIR}/benchmark.csv"

bash "${ROOT_DIR}/Dockersetup/run_bench.sh"

test -f "${OUT_CSV}"
echo "[smoke_test_docker] OK: ${OUT_CSV}"
