# JMH Benchmarks

This module contains microbenchmarks for FM4MC using the Java Microbenchmark Harness (JMH).

## Prerequisites
- Java 21 or later
- Gradle (wrapper included in the project)

## Required Test Data
The benchmarks expect the feature-model JSON files in `TestData/TestGraphs/TestFMJsons` relative to the project root `FM4MC/`.
Additional input data may be placed in `TestData/` and referenced via `JMHTestDataProvider` if needed.

## Running the Benchmarks
From the `FM4MC/` directory (the Gradle project root) JMH can be launched directly through Gradle:

```bash
./gradlew JMH:jmh
```

The results are written as CSV files (`onlineBenchmark.csv`, `onlineBenchmarkHuge.csv`, `onlySATSolver.csv`, etc.) in the `JMH/` module directory.
The CSV files use semicolon (`;`) as delimiter and for each measurement include columns such as benchmark name, mode, number of samples, score, error, and units.

## Create and Run a Standalone JMH JAR
Alternatively, an executable JAR can be built:

```bash
./gradlew JMH:jmhJar
```

The generated `JMH/build/libs/JMH-jmh.jar` can then be executed with the desired benchmark class, for example:

```bash
java -jar JMH/build/libs/JMH-jmh.jar Paper.Online.OnlineBenchmarkRunner -t 4 -o results.csv -rf CSV
```

Use `-o` to specify the output file; `-rf CSV` writes the results as CSV.
