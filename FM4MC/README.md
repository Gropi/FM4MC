# FM4MC Runtime

The `FM4MC` directory contains the Java implementation of the Edge-Flex runtime. It is organised as a multi-module Gradle project.

## Modules
- **Collector** – gathers hardware information from an edge node.
- **Configuration-Creator** – computes valid configurations and slices feature models.
- **Configuration-Manager** – aggregates collector data and builds configuration graphs.
- **Canete** – orchestrates the configuration process using the above modules.
- **Shared** – shared utilities and data structures.
- **JMH** – benchmarks for performance evaluation.
- **TestData** – sample inputs for testing.

## System Requirements
- OpenJDK 18 or newer
- Gradle (wrapper included)

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
