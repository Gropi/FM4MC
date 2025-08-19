# FM4MC Repository

FM4MC is a research prototype for managing and evaluating flexible microservice deployments on edge nodes. It collects hardware information from distributed devices and computes valid service configurations that can be orchestrated across the network.

## Repository Structure
- **FM4MC/** – Gradle-based Java code for the core runtime and utilities.
- **Protobuf/** – Protocol Buffer message definitions used for communication.
- **Eval/** – Evaluation data and experiments for microservice chains and storage strategies.

## System Requirements
- **Java**: OpenJDK 18 or newer
- **Build Tools**: Gradle (wrapper included)
- **Protocol Buffers**: `protoc` compiler if regenerating message classes

## Building
```bash
cd FM4MC
./gradlew build
```

## Running
See the [`FM4MC/README.md`](FM4MC/README.md) for details on starting the server and collectors.

## License
This project is released under the terms of the [LICENSE](LICENSE) file.
