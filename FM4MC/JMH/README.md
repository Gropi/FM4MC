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

## Interpreting Results
The CSV output contains one row per benchmark execution. A typical line looks like:

```
Benchmark;Mode;Cnt;Score;Error;Units
Paper.Online.OnlineBenchmarkRunner;avgt;5;123.456;7.890;ms/op
```

| Column      | Meaning                                                          |
|-------------|------------------------------------------------------------------|
| `Benchmark` | Fully qualified benchmark class name                             |
| `Mode`      | Measurement type (`avgt` = average time)                         |
| `Cnt`       | Number of measurement iterations                                 |
| `Score`     | Mean execution time per operation in milliseconds                |
| `Error`     | 99.9% confidence interval around the score                       |
| `Units`     | Time unit of `Score` and `Error` (usually `ms/op`)               |

Some benchmarks emit additional parameter columns (prefixed with `Param:`). Metadata for online-phase runs, such as the tested feature model, edge index, and requirement counts, is logged in `TestData/JMH_Online_Phase_Benchmark_Additional_Information/JMH_Online_Phase_Benchmark_Information.csv`. See the [TestData README](../TestData/README.md#jmh_online_phase_benchmark_additional_information) for column details.

For further background on these metrics, consult the [official JMH documentation](https://openjdk.org/projects/code-tools/jmh/).

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
