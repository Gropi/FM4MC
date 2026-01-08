@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "VENV_DIR=%SCRIPT_DIR%venv"
set "ACTIVATE=%VENV_DIR%\Scripts\activate.bat"
set "PYTHON_EXE=python"

REM --- Check: Python available?
%PYTHON_EXE% --version >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Python was not found. Please install Python 3 and ensure "python" is on PATH.
  exit /b 1
)

REM --- Create venv if missing
if not exist "%ACTIVATE%" (
  echo [INFO] No virtual environment found at "%VENV_DIR%". Creating venv...
  %PYTHON_EXE% -m venv "%VENV_DIR%"
  if errorlevel 1 (
    echo [ERROR] Failed to create venv. Ensure you have permission to write to this folder.
    exit /b 1
  )
)

REM --- Activate venv
call "%ACTIVATE%"
if errorlevel 1 (
  echo [ERROR] Failed to activate venv at "%ACTIVATE%".
  exit /b 1
)

REM --- Upgrade pip (optional but recommended)
python -m pip install --upgrade pip >nul 2>&1

REM --- Install dependencies if requirements.txt exists
if exist "%SCRIPT_DIR%requirements.txt" (
  echo [INFO] Installing Python dependencies from requirements.txt...
  python -m pip install -r "%SCRIPT_DIR%requirements.txt"
  if errorlevel 1 (
    echo [ERROR] Failed to install dependencies from requirements.txt.
    exit /b 1
  )
) else (
  echo [WARN] requirements.txt not found in "%SCRIPT_DIR%". Skipping dependency installation.
)

REM --- Ensure output directories exist
for %%d in (Results\RQ1a\Figure5 Results\RQ1b Results\RQ2) do (
  if not exist "%SCRIPT_DIR%%%d" mkdir "%SCRIPT_DIR%%%d"
)

REM --- Run scripts
for %%f in ("HeatMap-NoSlicing.py" "Heatmap_Threshold.py" "Online_Canete.py" "Online_FM4MC.py" "Storage_Use.py") do (
  echo [INFO] Running %%~f
  python "%SCRIPT_DIR%%%~f"
  if errorlevel 1 (
    echo [ERROR] Script failed: %%~f
    exit /b 1
  )
)

echo [INFO] Done. Figures are available under "%SCRIPT_DIR%Results\".
endlocal
