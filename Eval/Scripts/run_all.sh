#!/usr/bin/env bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
PYTHON="$SCRIPT_DIR/venv/bin/python"
# Ensure required output directories exist
RESULT_DIRS=(
  "$SCRIPT_DIR/Results/RQ1a/Figure5"
  "$SCRIPT_DIR/Results/RQ1b"
  "$SCRIPT_DIR/Results/RQ2"
)
for dir in "${RESULT_DIRS[@]}"; do
  mkdir -p "$dir"
done

SCRIPTS=(
  "HeatMap-NoSlicing.py"
  "Heatmap_Threshold.py"
  "Online_Canete.py"
  "Online_FM4MC.py"
  "Storage_Use.py"
)
for script in "${SCRIPTS[@]}"; do
  echo "Running $script"
  "$PYTHON" "$script"
done
