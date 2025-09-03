# FM4MC Repository

FM4MC is a research prototype for managing and evaluating flexible microservice deployments on edge nodes. It collects hardware information from distributed devices and computes valid service configurations that can be orchestrated across the network.

## Required Software
- **OS**: Windows 11 or Linux
- **CPU/RAM**: \u22654 cores, 16 GiB RAM recommended for full offline runs
- **Java**: OpenJDK 21+ (Gradle wrapper downloads dependencies)
- **Protocol Buffers**: `protoc` 3+ if regenerating message classes
- **Python 3**: for the scripts in `Eval/Scripts` (install packages via `pip`)
- **Docker** *(optional)*: to build and run containerized benchmarks in `Dockersetup/`

## Quick Start
```bash
cd FM4MC
./gradlew build
```
See [`FM4MC/README.md`](FM4MC/README.md) for starting the runtime, collectors and benchmarks.

## Repository Structure
- **FM4MC/** \u2013 Gradle-based Java modules (Configuration-Creator, Configuration-Manager, Canete, Collector, Shared utilities and JMH benchmarks)
- **Protobuf/** \u2013 Protocol Buffer message definitions for inter-component communication
- **Eval/** \u2013 Datasets and Python scripts for evaluating microservice chains and storage strategies
- **Dockersetup/** \u2013 Docker files and helper scripts for containerized builds and benchmarks
- **LICENSE** \u2013 Project license

## License
This project is released under the terms of the [LICENSE](LICENSE) file.

### How to cite
@inproceedings{FM4MC-ICSE2026,
author    = {Uwe Gropengie{\ss}er and Paul Wolfart and Julian Liphardt and Max M{\"u}hlh{\"a}user},
title     = {FM4MC: Improving Feature Models for Microservice Chains-Towards More Efficient Configuration and Validation},
booktitle = {Proceedings of the 48th IEEE/ACM International Conference on Software Engineering (ICSE)},
year      = {2026}
}

### Acknowledgments
Special thanks to Prof. Dr. Max Mühlhäuser for invaluable support in idea generation and discussions.
Julian Liphardt and Paul Wolfart for development contributions and conceptual discussions that shaped FM4MC.
