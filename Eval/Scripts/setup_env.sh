#!/usr/bin/env bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
python3 -m venv "$SCRIPT_DIR/venv"
source "$SCRIPT_DIR/venv/bin/activate"
python -m pip install --upgrade pip
pip install -r "$SCRIPT_DIR/requirements.txt"
echo "Virtual environment created in $SCRIPT_DIR/venv"
