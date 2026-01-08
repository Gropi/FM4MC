# Dockersetup — Docker Execution Workflow

This directory contains the **authoritative execution workflow** for the FM4MC artifact.
For ICSE AE, reviewers should use these scripts to build the container and run the benchmarks.

The workflow supports two modes:
- `smoke` — quick functional check
- `full` — paper-level benchmark configuration (may run substantially longer depending on hardware and solver behavior)

---

## Contents

- `Dockerfile`  
  Defines the containerized build environment.

- `build_and_run.sh`  
  Builds the Docker image and executes it in either `smoke` or `full` mode (Linux/macOS; also works on Windows via Git Bash/WSL).

- `build_and_run.bat`  
  Windows wrapper for the same workflow (CMD/PowerShell).

- `output/`  
  Host directory where the container writes benchmark CSV results.

---

## Prerequisites

- Docker Engine / Docker Desktop installed and running
- On Windows, Docker Desktop with a working backend (recommended: WSL2)

---

## Usage

### Smoke Test
Purpose:
- demonstrate that the artifact builds and runs end-to-end
- produce a small, representative output that confirms correct execution

**Linux/macOS (or Windows via Git Bash/WSL):**
```bash
bash Dockersetup/build_and_run.sh smoke
```

**Windows (CMD/PowerShell):**
```bat
Dockersetup\build_and_run.bat smoke
```

Outputs:
- CSV files under `Dockersetup/output/` (smoke subset)

---

### Full Evaluation
Purpose:
- reproduce the benchmark configuration used for the paper

**Linux/macOS (or Windows via Git Bash/WSL):**
```bash
bash Dockersetup/build_and_run.sh full
```

**Windows (CMD/PowerShell):**
```bat
Dockersetup\build_and_run.bat full
```

Outputs:
- CSV files under `Dockersetup/output/` (full suite)

---

## Output format
Outputs are written as CSV files (JMH result format) to the host-mounted directory:
- `Dockersetup/output/`

The evaluation scripts under `Eval/Scripts/` consume CSV files and regenerate paper figures.

--- 

### JMH Columns in This Project

The JMH result files used in this evaluation follow the schema below (column order may vary slightly between runs):

| Column | Meaning |
|---|---|
| `Benchmark` | Fully qualified benchmark method name |
| `Mode` | JMH measurement mode (see below) |
| `Threads` | Number of benchmark threads used |
| `Samples` | Number of measurement iterations (after warm-up) |
| `Score` | Aggregated performance metric reported by JMH |
| `Score Error (99,9%)` | Half-width of the 99.9% confidence interval |
| `Unit` | Unit of the score (e.g., `ms/op`) |
| `Param: ...` | Benchmark parameters (vary by benchmark and run) |

---

## Reproducibility notes
- Docker provides a controlled environment and is strongly recommended for artifact evaluation.
- Benchmark runtimes vary across machines due to SAT solver performance; see the repository-level disclaimer in `README.md`.