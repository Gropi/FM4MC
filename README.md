# FM4MC Repository

FM4MC is a research prototype for managing and evaluating flexible microservice deployments on edge nodes. It collects hardware information from distributed devices and computes valid service configurations that can be orchestrated across the network.

## Repository Structure
- **FM4MC/** – Gradle-based Java code for the core runtime and utilities.
- **Protobuf/** – Protocol Buffer message definitions used for communication.
- **Eval/** – Evaluation data and experiments for microservice chains and storage strategies.

## System Requirements
- **OS**: Windows 11, Linux should be possible, too.
- **CPU/RAM**: ≥4 cores, 16 GiB RAM recommended for full offline runs
- **Java**: OpenJDK **21**+ (Gradle wrapper included)
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
