# JMH — Benchmarks

This module contains the Java Microbenchmark Harness (JMH) benchmarks used in the FM4MC evaluation.

For ICSE AE, the recommended way to run benchmarks is Docker via `Dockersetup/`,
which builds the project and executes the benchmark configuration in a controlled environment.

---

## Native prerequisites
- Java 21+
- Gradle wrapper included

---

## Build and run via Gradle (native)
From the FM4MC Gradle root (`FM4MC/`):

```bash
./gradlew :JMH:jmh
```

This executes the benchmarks using the JMH Gradle plugin.

---

## Build an executable JMH JAR (native)
From `FM4MC/`:

```bash
./gradlew :JMH:jmhJar
```

The generated executable JAR is located under:
- `JMH/build/libs/`

To list available benchmarks:
```bash
java -jar JMH/build/libs/*-jmh.jar -l
```

To run benchmarks and write results as CSV:
```bash
java -jar JMH/build/libs/*-jmh.jar -rf csv -rff ./benchmark.csv
```

---

## Run a concrete benchmark

There are several runners available in the JMH soultion space. To run them you can as following:

```bash
java -jar JMH/build/libs/JMH-jmh.jar Paper.Online.OnlineBenchmarkRunner -t 4 -o results.csv -rf CSV
```

Use `-o` to specify the output file; `-rf CSV` writes the results as CSV.

---

## Benchmark inputs
Benchmark inputs are located at:
- `FM4MC/TestData/`

Relative path assumptions depend on the working directory. For reproducibility, prefer Docker (`Dockersetup/`).
