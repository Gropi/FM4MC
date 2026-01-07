#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/Dockersetup/output"
mkdir -p "${OUT_DIR}"

# Defaults for "full" runs; adjust to match paper settings if needed
export THREADS="${THREADS:-1}"
export WARMUP_ITERS="${WARMUP_ITERS:-5}"
export MEAS_ITERS="${MEAS_ITERS:-5}"
export FORKS="${FORKS:-1}"

export BENCH_REGEX="${BENCH_REGEX:-.*}"
export OUT_CSV="${OUT_DIR}/benchmark.csv"

bash "${ROOT_DIR}/Dockersetup/run_bench.sh"

test -f "${OUT_CSV}"
echo "[run_benchmarks_docker] OK: ${OUT_CSV}"
