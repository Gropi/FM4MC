#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------
# Full benchmark suite for FM4MC (Paper experiments)
# ------------------------------------------------------------

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_BASE="${ROOT_DIR}/Dockersetup/output/full"
mkdir -p "${OUT_BASE}"

# Optional Docker-mounted volume for final results
OUTPUT_VOLUME="/output"
mkdir -p "${OUTPUT_VOLUME}"

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
RUNNER_DIR="${ROOT_DIR}/FM4MC/JMH"
cd "${RUNNER_DIR}"

for RUNNER in "${RUNNERS[@]}"; do
  NAME="$(basename "${RUNNER}")"
  OUT_DIR="${OUT_BASE}/${NAME}"
  mkdir -p "${OUT_DIR}"

  echo
  echo "============================================================"
  echo "[run_bench] Running ${RUNNER}"
  echo "[run_bench] Output dir: ${OUT_DIR}"
  echo "============================================================"

  # Optional: single RESULT_PATH if the runner supports it
  export RESULT_PATH="${OUT_DIR}/results.csv"

  java -cp "${JMH_JAR}" "${RUNNER}"

  # ------------------------------------------------------------
  # Move all CSVs from runner directory to OUT_DIR
  # ------------------------------------------------------------
  CSV_FILES=$(find "${RUNNER_DIR}" -maxdepth 1 -name "*.csv" || true)

  if [[ -z "${CSV_FILES}" ]]; then
    echo "[run_bench] WARNING: ${RUNNER} did not produce CSV files in ${RUNNER_DIR}"
  else
    for csv in ${CSV_FILES}; do
      mv "${csv}" "${OUT_DIR}/"
      echo "[run_bench] Moved: $(basename "${csv}") -> ${OUT_DIR}"
    done
  fi

  # Optional: copy to Docker-mounted volume
  if [[ -d "${OUTPUT_VOLUME}" ]]; then
    cp "${OUT_DIR}"/*.csv "${OUTPUT_VOLUME}/"
    echo "[run_bench] Copied CSVs to Docker-mounted volume ${OUTPUT_VOLUME}"
  fi

  echo "[run_bench] DONE: ${RUNNER}"
done

echo
echo "[run_bench] All benchmarks completed successfully."
echo "[run_bench] Results stored under: ${OUT_BASE}"
