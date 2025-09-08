@echo off
REM Build the FM4MC Docker image and run a component or benchmarks, collecting results
setlocal
set IMAGE_NAME=fm4mc
set MODE=%1
if "%MODE%"=="" set MODE=jmh
REM Determine script directory
set "SCRIPT_DIR=%~dp0"
for %%i in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fi"
REM Ensure Docker daemon is reachable
docker info >nul 2>&1
if errorlevel 1 (
  echo Docker daemon not running. Attempting to start Docker Desktop...
  set "DOCKER_DESKTOP=%ProgramFiles%\Docker\Docker\Docker Desktop.exe"
  if not exist "%DOCKER_DESKTOP%" set "DOCKER_DESKTOP=%ProgramFiles(x86)%\Docker\Docker\Docker Desktop.exe"
  start "" "%DOCKER_DESKTOP%"
  echo Waiting for Docker Desktop to start...
  timeout /t 60 >nul
  docker info >nul 2>&1
  if errorlevel 1 (
    echo Docker still not running. Please start Docker Desktop and try again.
    exit /b 1
  )
)
docker build -f "%SCRIPT_DIR%Dockerfile" -t %IMAGE_NAME% "%ROOT_DIR%"
if not exist "%SCRIPT_DIR%output" mkdir "%SCRIPT_DIR%output"
docker run --rm -v "%SCRIPT_DIR%output:/output" %IMAGE_NAME% %MODE%
echo Results stored in Dockersetup\output
