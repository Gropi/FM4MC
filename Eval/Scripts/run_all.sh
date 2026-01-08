#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

VENV_DIR="$SCRIPT_DIR/venv"
PYTHON_VENV="$VENV_DIR/bin/python"

# --- Check: Python 3 available?
if command -v python3 >/dev/null 2>&1; then
  PYTHON_SYS="python3"
elif command -v python >/dev/null 2>&1; then
  # Fallback: python, but ensure it's Python 3
  if python -c 'import sys; sys.exit(0 if sys.version_info.major==3 else 1)' >/dev/null 2>&1; then
    PYTHON_SYS="python"
  else
    echo "[ERROR] 'python' is not Python 3. Please install Python 3 and ensure python3/python is available." >&2
    exit 1
  fi
else
  echo "[ERROR] Python 3 was not found. Please install Python 3 (python3) and ensure it is on PATH." >&2
  exit 1
fi

# --- Create venv if missing
if [[ ! -x "$PYTHON_VENV" ]]; then
  echo "[INFO] No venv found at '$VENV_DIR'. Creating virtual environment..."
  "$PYTHON_SYS" -m venv "$VENV_DIR"
fi

# --- Upgrade pip (best-effort)
"$PYTHON_VENV" -m pip install --upgrade pip >/dev/null 2>&1 || true

# --- Install dependencies if requirements.txt exists
if [[ -f "$SCRIPT_DIR/requirements.txt" ]]; then
  echo "[INFO] Installing Python dependencies from requirements.txt..."
  "$PYTHON_VENV" -m pip install -r "$SCRIPT_DIR/requirements.txt"
else
  echo "[WARN] requirements.txt not found in '$SCRIPT_DIR'. Skipping dependency installation."
fi

# --- Ensure required output directories exist
RESULT_DIRS=(
  "$SCRIPT_DIR/Results/RQ1a/Figure5"
  "$SCRIPT_DIR/Results/RQ1b"
  "$SCRIPT_DIR/Results/RQ2"
)
for dir in "${RESULT_DIRS[@]}"; do
  mkdir -p "$dir"
done

# --- Run scripts
SCRIPTS=(
  "HeatMap-NoSlicing.py"
  "Heatmap_Threshold.py"
  "Online_Canete.py"
  "Online_FM4MC.py"
  "Storage_Use.py"
)

for script in "${SCRIPTS[@]}"; do
  if [[ ! -f "$SCRIPT_DIR/$script" ]]; then
    echo "[ERROR] Script not found: $SCRIPT_DIR/$script" >&2
    exit 1
  fi
  echo "[INFO] Running $script"
  "$PYTHON_VENV" "$SCRIPT_DIR/$script"
done

echo "[INFO] Done. Outputs are available under '$SCRIPT_DIR/Results/'."
