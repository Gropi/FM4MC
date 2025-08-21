# Docker Setup for FM4MC

This directory contains files to build and run FM4MC inside a Docker container.

## Contents
- `Dockerfile` – builds FM4MC with Gradle and packages the runtime jars with required test data.
- `run_bench.sh` – default entry point inside the container; runs JMH benchmarks and stores the results in `/output`.
- `build_and_run.sh` – Linux helper script for building the image and running the container while collecting benchmark results.
- `build_and_run.ps1` – Windows PowerShell script performing the same workflow.

## Usage
### Linux/macOS
```bash
./Dockersetup/build_and_run.sh
```

### Windows
```powershell
powershell -ExecutionPolicy Bypass -File .\Dockersetup\build_and_run.ps1
```
The benchmark CSV is written to `Dockersetup/output/benchmark.csv`.
