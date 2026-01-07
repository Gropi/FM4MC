# JMH Benchmarks

This module contains microbenchmarks for FM4MC using the Java Microbenchmark Harness (JMH).

## Prerequisites
- Java 21 or later
- Gradle (wrapper included in the project)

## Required Test Data
The benchmarks expect the feature-model JSON files in `FM4MC/TestData/TestGraphs/TestFMJsons` relative to the project root.

> Note: Some evaluation outputs contain file path parameters (e.g., `Param: _FilePathFM`) that reference these JSON files. For understanding the evaluation results, the concrete contents of `TestData/` are typically not required.

## Running the Benchmarks
From the `FM4MC/` directory (the Gradle project root) JMH can be launched directly through Gradle:

```bash
./gradlew JMH:jmh
```

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

---