#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------
# Full benchmark suite for FM4MC (Paper experiments)
# ------------------------------------------------------------

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_BASE="${ROOT_DIR}/Dockersetup/output/full"
mkdir -p "${OUT_BASE}"

echo "[run_bench] ROOT_DIR=${ROOT_DIR}"
echo "[run_bench] Output base directory: ${OUT_BASE}"

# ------------------------------------------------------------
# Locate JMH uber-jar
# ------------------------------------------------------------
JMH_JAR="$(ls -1 "${ROOT_DIR}/FM4MC/JMH/build/libs/"*all.jar | head -n 1)"
if [[ -z "${JMH_JAR}" ]]; then
  echo "[run_bench] ERROR: JMH uber-jar (*all.jar) not found"
  exit 1
fi

echo "[run_bench] Using JMH jar: ${JMH_JAR}"

# ------------------------------------------------------------
# List of benchmark runners (paper order)
# ------------------------------------------------------------
RUNNERS=(
  "Paper.Offline.OfflineBenchmarkRunner"
  "Paper.Online.CaneteBenchmarkRunner"
  "Paper.Online.OnlineBenchmarkRunner"
  "Paper.SlicingBenchmark.SlicingBenchmarkRunner"
)

# ------------------------------------------------------------
# Execute runners sequentially
# ------------------------------------------------------------
cd "${ROOT_DIR}/FM4MC/JMH"

for RUNNER in "${RUNNERS[@]}"; do
  NAME="$(basename "${RUNNER}")"
  OUT_DIR="${OUT_BASE}/${NAME}"
  mkdir -p "${OUT_DIR}"

  echo
  echo "============================================================"
  echo "[run_bench] Running ${RUNNER}"
  echo "[run_bench] Output dir: ${OUT_DIR}"
  echo "============================================================"

  export RESULT_PATH="${OUT_DIR}/results.csv"

  java -cp "${JMH_JAR}" "${RUNNER}"

  if [[ ! -f "${RESULT_PATH}" ]]; then
    echo "[run_bench] ERROR: ${RUNNER} did not produce ${RESULT_PATH}"
    exit 1
  fi

  echo "[run_bench] DONE: ${RUNNER}"
done

echo
echo "[run_bench] All benchmarks completed successfully."
echo "[run_bench] Results stored under: ${OUT_BASE}"
