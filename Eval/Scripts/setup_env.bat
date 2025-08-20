@echo off
set SCRIPT_DIR=%~dp0
python -m venv "%SCRIPT_DIR%venv"
call "%SCRIPT_DIR%venv\Scripts\activate"
python -m pip install --upgrade pip
pip install -r "%SCRIPT_DIR%requirements.txt"
echo Virtual environment created in %SCRIPT_DIR%venv
