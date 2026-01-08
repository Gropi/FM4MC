#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------
# Build the FM4MC Docker image and run smoke or full benchmarks
# ------------------------------------------------------------

IMAGE_NAME="fm4mc"

# Determine script directory and repo root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "[build_and_run] SCRIPT_DIR=${SCRIPT_DIR}"
echo "[build_and_run] ROOT_DIR=${ROOT_DIR}"

# ------------------------------------------------------------
# Ensure Docker daemon is reachable
# ------------------------------------------------------------
if ! docker info >/dev/null 2>&1; then
  echo "[build_and_run] ERROR: Docker daemon not running."
  echo "[build_and_run] Please start Docker and retry."
  exit 1
fi

# ------------------------------------------------------------
# Build Docker image
# ------------------------------------------------------------
echo "[build_and_run] Building Docker image: ${IMAGE_NAME}"
docker build -f "${SCRIPT_DIR}/Dockerfile" -t "${IMAGE_NAME}" "${ROOT_DIR}"

# ------------------------------------------------------------
# Prepare output directory
# ------------------------------------------------------------
OUTPUT_DIR="${SCRIPT_DIR}/output"
mkdir -p "${OUTPUT_DIR}"

# ------------------------------------------------------------
# Determine run mode (default: smoke)
# ------------------------------------------------------------
MODE="${1:-smoke}"

echo "[build_and_run] Run mode: ${MODE}"

case "${MODE}" in
  smoke)
    echo "[build_and_run] Running SMOKE benchmarks..."
    docker run --rm \
      -v "${OUTPUT_DIR}:/output" \
      "${IMAGE_NAME}"
    ;;
  full)
    echo "[build_and_run] Running FULL benchmarks..."
    docker run --rm \
      -v "${OUTPUT_DIR}:/output" \
      "${IMAGE_NAME}" \
      bash Dockersetup/run_bench.sh
    ;;
  *)
    echo "[build_and_run] ERROR: Unknown mode '${MODE}'"
    echo "Usage: ./build_and_run.sh [smoke|full]"
    exit 1
    ;;
esac

echo
echo "[build_and_run] Results stored in Dockersetup/output"
