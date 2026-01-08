#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------
# Smoke test for FM4MC
# - very small configuration
# - executes a dedicated SmokeTestRunner
# - finishes within seconds
# - moves all CSV results to OUT_DIR for Docker host
# ------------------------------------------------------------

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/Dockersetup/output/smoke"
mkdir -p "${OUT_DIR}"

echo "[smoke] ROOT_DIR=${ROOT_DIR}"
echo "[smoke] OUT_DIR=${OUT_DIR}"

# Optional: Docker-mounted output (if needed)
OUTPUT_VOLUME="/output"
mkdir -p "${OUTPUT_VOLUME}"

# ------------------------------------------------------------
# Locate JMH uber-jar
# ------------------------------------------------------------
JMH_JAR="$(ls -1 "${ROOT_DIR}/FM4MC/JMH/build/libs/"*all.jar | head -n 1)"
if [[ -z "${JMH_JAR}" ]]; then
  echo "[smoke] ERROR: JMH uber-jar (*all.jar) not found"
  exit 1
fi
echo "[smoke] Using JMH jar: ${JMH_JAR}"

# ------------------------------------------------------------
# Run dedicated smoke runner
# ------------------------------------------------------------
RUNNER_DIR="${ROOT_DIR}/FM4MC/JMH"
cd "${RUNNER_DIR}"
echo "[smoke] Running SmokeTestRunner in ${RUNNER_DIR}..."
java -cp "${JMH_JAR}" Artifacts.SmokeTestRunner

# ------------------------------------------------------------
# Move all CSV results to OUT_DIR
# ------------------------------------------------------------
CSV_FILES=$(find "${RUNNER_DIR}" -maxdepth 1 -name "*.csv" || true)

if [[ -z "${CSV_FILES}" ]]; then
  echo "[smoke] ERROR: No CSV results produced by SmokeTestRunner"
  exit 1
fi

for csv in ${CSV_FILES}; do
    mv "${csv}" "${OUT_DIR}/"
    echo "[smoke] Moved: $(basename "${csv}") -> ${OUT_DIR}"
done

echo "[smoke] All CSV results are now in ${OUT_DIR}"

# Optional: copy to Docker volume if mounted
if [[ -d "${OUTPUT_VOLUME}" ]]; then
    cp "${OUT_DIR}"/*.csv "${OUTPUT_VOLUME}/"
    echo "[smoke] Also copied CSVs to Docker-mounted volume ${OUTPUT_VOLUME}"
fi

echo "[smoke] DONE"
