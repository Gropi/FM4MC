#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------
# Smoke test for FM4MC
# - very small configuration
# - executes a dedicated SmokeTestRunner
# - finishes within seconds
# ------------------------------------------------------------

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/Dockersetup/output/smoke"
mkdir -p "${OUT_DIR}"

echo "[smoke] ROOT_DIR=${ROOT_DIR}"
echo "[smoke] OUT_DIR=${OUT_DIR}"


# Optional override (e.g., for debugging)
export RESULT_PATH="${RESULT_PATH:-${OUT_DIR}/benchmark_smoke.csv}"

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
cd "${ROOT_DIR}/FM4MC/JMH"

java -cp "${JMH_JAR}" Artifacts.SmokeTestRunner

# ------------------------------------------------------------
# Verify result
# ------------------------------------------------------------
if [[ ! -f "${RESULT_PATH}" ]]; then
  echo "[smoke] ERROR: Smoke benchmark did not produce result CSV"
  exit 1
fi

echo "[smoke] OK: ${RESULT_PATH}"
