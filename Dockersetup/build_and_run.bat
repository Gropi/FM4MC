@echo off
REM Build the FM4MC Docker image and run benchmarks, collecting results
setlocal
set IMAGE_NAME=fm4mc
REM Determine script directory
set "SCRIPT_DIR=%~dp0"
for %%i in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fi"
docker build -f "%SCRIPT_DIR%Dockerfile" -t %IMAGE_NAME% "%ROOT_DIR%"
if not exist "%SCRIPT_DIR%output" mkdir "%SCRIPT_DIR%output"
docker run --rm -v "%SCRIPT_DIR%output:/output" %IMAGE_NAME%
echo Benchmark results stored in Dockersetup\output\benchmark.csv
