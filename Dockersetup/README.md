# Docker Setup for FM4MC

This directory contains files to build and run FM4MC inside a Docker container.

## Contents
- `Dockerfile` – builds FM4MC with Gradle and packages the runtime jars with required test data.
- `run_bench.sh` – default entry point inside the container; runs JMH benchmarks and stores the results in `/output`.
- `build_and_run.sh` – Bash helper script that builds the image and runs the container while collecting benchmark results.
- `build_and_run.bat` – Windows batch script performing the same build-and-run workflow.

## Usage
Ensure Docker is installed and the daemon is running (Docker Desktop on Windows).
### Linux / macOS

Run the helper script with Bash (on Windows, use Git Bash or WSL):

```bash
bash ./Dockersetup/build_and_run.sh
```

### Windows

Run the batch script from a Command Prompt or PowerShell window:

```bat
Dockersetup\build_and_run.bat
```

The benchmark CSV is written to `Dockersetup/output/benchmark.csv`.
