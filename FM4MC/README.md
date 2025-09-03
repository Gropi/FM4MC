# FM4MC Runtime

**FM4MC** is a research prototype that validates large feature models for microservice chains efficiently by slicing them into Partial Feature Models (PFMs) and selectively applying SAT solving. This repository contains the **artifact** accompanying our ICSE’26 paper.

## Modules
- **Configuration-Creator** – computes valid configurations and slices feature models.
- **Configuration-Manager** – aggregates collector data and builds configuration graphs.
- **Canete** – orchestrates the configuration process using the above modules.
- **Shared** – shared utilities and data structures.
- **JMH** – benchmarks for performance evaluation.
- **TestData** – sample inputs for testing.

## Building
Use the Gradle wrapper to compile all modules:
```bash
./gradlew build
```

## JMH Benchmarks
Benchmarks for performance evaluation reside in the `JMH` module. Run them with:

```bash
./gradlew JMH:jmh
```

The benchmarks expect their input files in `TestData/` (see [`JMH/README.md`](JMH/README.md) for details) and write semicolon-separated CSV results such as `onlineBenchmark.csv` into the `JMH` directory.