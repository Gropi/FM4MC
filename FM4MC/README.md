# FM4MC Runtime

**FM4MC** is a research prototype that validates large feature models for microservice chains efficiently by slicing them into Partial Feature Models (PFMs) and selectively applying SAT solving. This repository contains the **artifact** accompanying our ICSE’26 paper.

## Modules
- **Collector** – gathers hardware information from an edge node.
- **Configuration-Creator** – computes valid configurations and slices feature models.
- **Configuration-Manager** – aggregates collector data and builds configuration graphs.
- **Canete** – orchestrates the configuration process using the above modules.
- **Shared** – shared utilities and data structures.
- **JMH** – benchmarks for performance evaluation.
- **TestData** – sample inputs for testing.

## System Requirements
- **OS**: Windows 11, Linux should be possible, too.
- **CPU/RAM**: ≥4 cores, 16 GiB RAM recommended for full offline runs
- **Java**: OpenJDK **21**+ (Gradle wrapper included)

## Building
Use the Gradle wrapper to compile all modules:
```bash
./gradlew build
```

## Running the Software
The runtime consists of a server and multiple collectors.

### Server
Start the server with:
```bash
java -jar Canete/build/libs/Canete.jar -s [-p <port>]
```

### Collector
Each edge node runs a collector:
```bash
java -jar Collector/build/libs/Collector.jar -c [-p <server port>] [-i <server ip address>]
```

## JMH Benchmarks
Benchmarks for performance evaluation reside in the `JMH` module. Run them with:

```bash
./gradlew JMH:jmh
```

The benchmarks expect their input files in `TestData/` (see [`JMH/README.md`](JMH/README.md) for details) and write semicolon-separated CSV results such as `onlineBenchmark.csv` into the `JMH` directory.

### How to cite
@inproceedings{FM4MC-ICSE2026,
    author    = {Uwe Gropengie{\ss}er and Paul Wolfart and Julian Liphardt and Max M{\"u}hlh{\"a}user},
    title     = {FM4MC: Improving Feature Models for Microservice Chains—Towards More Efficient Configuration and Validation},
    booktitle = {Proceedings of the 48th IEEE/ACM International Conference on Software Engineering (ICSE)},
    year      = {2026}
}

### Acknowledgments
Special thanks to Prof. Dr. Max Mühlhäuser for invaluable support in idea generation and discussions.
Julian Liphard and Paul Wolfart for development contributions and conceptual discussions that shaped FM4MC.