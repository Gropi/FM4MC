#!/usr/bin/env bash
set -euo pipefail

# Dockersetup/run_bench.sh
# Runs JMH benchmarks and writes a single CSV to Dockersetup/output/.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/Dockersetup/output"
mkdir -p "${OUT_DIR}"

# Configurable parameters
BENCH_REGEX="${BENCH_REGEX:-.*}"
THREADS="${THREADS:-1}"
WARMUP_ITERS="${WARMUP_ITERS:-1}"
MEAS_ITERS="${MEAS_ITERS:-1}"

# Optional: set forks to 1 for more predictable runtime; can be overridden
FORKS="${FORKS:-1}"

# Output file
OUT_CSV="${OUT_CSV:-${OUT_DIR}/benchmark.csv}"

echo "[run_bench] ROOT_DIR=${ROOT_DIR}"
echo "[run_bench] BENCH_REGEX=${BENCH_REGEX}"
echo "[run_bench] THREADS=${THREADS} WARMUP_ITERS=${WARMUP_ITERS} MEAS_ITERS=${MEAS_ITERS} FORKS=${FORKS}"
echo "[run_bench] OUT_CSV=${OUT_CSV}"

# Ensure we run with repo root as working directory so relative paths in benchmarks are stable
cd "${ROOT_DIR}"

# Locate JMH jar (adjust if your build produces a different name)
JMH_JAR="$(ls -1 "${ROOT_DIR}/FM4MC/JMH/build/libs/"*all.jar | head -n 1)"
if [[ -z "${JMH_JAR}" ]]; then
  echo "[run_bench] ERROR: could not locate JMH uber-jar under FM4MC/JMH/build/libs/"
  exit 1
fi
echo "[run_bench] Using JMH jar: ${JMH_JAR}"

# Run JMH
cd "${ROOT_DIR}/FM4MC/JMH"
java -cp "${JMH_JAR}" Artifacts.SmokeTestRunner

echo "[run_bench] Done. Results at: ${OUT_CSV}"
