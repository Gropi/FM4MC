# FM4MC — Implementation (Java/Gradle)

This directory contains the Java implementation of FM4MC as a multi-module Gradle project.
It includes the JMH benchmark suite and benchmark input data.

For ICSE AE, the recommended execution path is Docker via `Dockersetup/`.
This README documents the native build and the project structure for reuse and extension.

---

## Modules (overview)
- `Configuration-Creator/` — slicing and configuration creation
- `Configuration-Manager/` — configuration graph generation and aggregation
- `Canete/` — orchestration logic integrating the modules
- `Shared/` — shared utilities and data structures
- `JMH/` — benchmark suite used for evaluation
- `TestData/` — benchmark inputs (feature models and auxiliary files)

---

## Build (native)
From this directory:

```bash
./gradlew build
```

This builds all modules and produces artifacts under each module’s `build/` folder.

---

## Benchmark inputs
Benchmark input data is stored under:
- `FM4MC/TestData/`

Benchmarks use these inputs during execution. The Docker workflow ensures that the runtime layout is stable and that
relative paths resolve correctly.

---

## JMH benchmarks
See:
- `FM4MC/JMH/README.md`

---

## Reuse / extension
- Add or modify feature-model inputs in `TestData/`.
- Extend or add benchmarks under `JMH/src/jmh/`.
- Rebuild and rerun using either Docker (`Dockersetup/`) or native execution.
