# FM4MC Artifact (ICSE 2026)

This repository contains the research artifact for the ICSE 2026 paper:

**FM4MC: Improving Feature Models for Microservice Chains — Towards More Efficient Configuration and Validation**

The artifact provides:
- the FM4MC implementation (Java/Gradle),
- benchmark inputs (feature models and auxiliary data),
- a Docker-based workflow to build and execute the experiments, and
- evaluation material (raw data + scripts + generated figures).

---

## Purpose

FM4MC is a research prototype that validates large feature models for microservice chains efficiently by slicing them into Partial Feature Models (PFMs) and selectively applying SAT solving. The artifact enables reviewers to (i) run the benchmark suite and (ii) inspect / regenerate the evaluation figures.

---

## Badges claimed (ICSE)

- **Artifacts Available**: the artifact will be released via an archival repository providing a persistent identifier (DOI). GitHub alone is not considered archival.
  - **Archival link/DOI**: https://doi.org/10.5281/zenodo.15714242
  - **Evaluated commit**: cf23bdde3aceeadabc8f1d5b6b704846a9524446

- **Artifacts Evaluated – Reusable**: the artifact is documented, consistent, complete, and exercisable, and is structured to support reuse/repurposing (Docker workflow, clear outputs, reusable evaluation pipeline).

---

## Quickstart (recommended)

### Prerequisites
- Docker Engine / Docker Desktop (Linux/macOS/Windows)

### Run the artifact via the provided scripts
All execution entry points are located in `Dockersetup/`. We include smoke tests to verify basic functionality. 
However, due to their long execution time, we do not run the standalone tests for the SAT solver, as these require a 
significant amount of time even with the smallest configurations (see paper). The same applies to the storage tests, 
since they represent a static test scenario in which a smaller smoke test would not be meaningful. The complete test 
suite is available, and the data presented in the paper can also be found under `Eval/`.

**Smoke test (functional check):**
```bash
bash Dockersetup/build_and_run.sh smoke
```
Windows (CMD/PowerShell):
```bat
Dockersetup\build_and_run.bat smoke
```

**Full evaluation (paper-level benchmarks):**
```bash
bash Dockersetup/build_and_run.sh full
```
Windows (CMD/PowerShell):
```bat
Dockersetup\build_and_run.bat full
```

**Outputs (expected):**
- Benchmark CSV results are written under `Dockersetup/output/` (see `Dockersetup/README.md` for the exact file layout).
- Figures can be inspected and regenerated under `Eval/` (see `Eval/README.md` and `Eval/Scripts/README.md`).

**JMH Columns in This Project:**

The JMH result files used in this evaluation follow the schema below (column order may vary slightly between runs):

| Column | Meaning                                          |
|---|--------------------------------------------------|
| `Benchmark` | Fully qualified benchmark method name            |
| `Mode` | JMH measurement mode (e.g., single shot [ss])    |
| `Threads` | Number of benchmark threads used                 |
| `Samples` | Number of measurement iterations (after warm-up) |
| `Score` | Aggregated performance metric reported by JMH    |
| `Score Error (99,9%)` | Half-width of the 99.9% confidence interval      |
| `Unit` | Unit of the score (e.g., `ms/op`)                |
| `Param: ...` | Benchmark parameters (vary by benchmark and run) |

---

## Setup

### Software
- **Recommended:** Docker Engine / Docker Desktop.
- **Optional (native):** Java 21+ (Gradle wrapper included). Plot regeneration requires Python 3 (see `Eval/Scripts/README.md`).

### Hardware (recommended)
Because experiments include SAT solving, hardware influences absolute runtime.

**Tier A (Smoke test / functional verification)**
- CPU: >= 4 modern cores
- RAM: >= 16 GB
- Disk: a few GB free space

**Tier B (Full evaluation / paper-level experiments)**
- CPU: >= 8 modern cores recommended
- RAM: >= 32 GB recommended
- Disk: ~10 GB free space recommended (build cache + results)

No GPU is required.

---

## Performance disclaimer (SAT solver)

Benchmarks rely on SAT solving and therefore exhibit hardware-dependent runtimes. Absolute execution times can vary with CPU architecture and core count, memory bandwidth, RAM size, JVM/OS characteristics, and background load.

**Expected behavior across machines:** while absolute values may shift, the qualitative differences and relative trends between configurations should remain observable within similar orders of magnitude.

---

## Usage (details)

- **Docker execution workflow:** `Dockersetup/README.md`
- **Evaluation data and figures:** `Eval/README.md`
- **Figure regeneration pipeline:** `Eval/Scripts/README.md`
- **Implementation notes (native build):** `FM4MC/README.md`

---

## License and redistribution

- See `LICENSE` for distribution rights.
- Evaluation data and generated figures are distributed under the repository license unless explicitly noted otherwise within subdirectories.

---

## Citation

```bibtex
@inproceedings{FM4MC-ICSE2026,
  author    = {Uwe Gropengie{\\ss}er and Paul Wolfart and Julian Liphardt and Max M{\\\"u}hlh{\\\"a}user},
  title     = {FM4MC: Improving Feature Models for Microservice Chains—Towards More Efficient Configuration and Validation},
  booktitle = {Proceedings of the 48th IEEE/ACM International Conference on Software Engineering (ICSE)},
  year      = {2026}
}
```

### Acknowledgments
Special thanks to Prof. Dr. Max Mühlhäuser for invaluable support in idea generation and discussions.
Julian Liphardt and Paul Wolfart for development contributions and conceptual discussions that shaped FM4MC.
