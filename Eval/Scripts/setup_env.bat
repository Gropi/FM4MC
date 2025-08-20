@echo off
setlocal
set SCRIPT_DIR=%~dp0
set VENV_DIR=%SCRIPT_DIR%venv
set PY_DIR=%SCRIPT_DIR%python
set PYTHON_EXE=%PY_DIR%\python.exe

if not exist "%PYTHON_EXE%" (
    echo Python not found. Downloading local copy...
    set PY_VERSION=3.11.7
    set PY_INSTALLER=python-%PY_VERSION%-amd64.exe
    powershell -Command "Invoke-WebRequest -Uri https://www.python.org/ftp/python/%PY_VERSION%/%PY_INSTALLER% -OutFile '%SCRIPT_DIR%%PY_INSTALLER%'"
    "%SCRIPT_DIR%%PY_INSTALLER%" /quiet InstallAllUsers=0 PrependPath=0 Include_pip=1 TargetDir="%PY_DIR%"
    del "%SCRIPT_DIR%%PY_INSTALLER%"
)

"%PYTHON_EXE%" -m venv "%VENV_DIR%"
call "%VENV_DIR%\Scripts\activate"
"%VENV_DIR%\Scripts\python.exe" -m pip install --upgrade pip
pip install -r "%SCRIPT_DIR%requirements.txt"
echo Virtual environment created in %VENV_DIR%