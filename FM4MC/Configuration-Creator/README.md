# Configuration Creator

Slices large feature models into smaller partial models and computes valid configurations for each part. The resulting CSV files can be reused by other modules such as the Configuration Manager.

## Feature Model Format
Feature models are described in JSON with three top-level sections:
- `features` – list of features with hardware annotations and child relations
- `featureConnectivityInformation` – describes execution order including the `startFeature`
- `crossTreeConstraints` – global `requires` and `excludes` relations between features

The current parser supports mandatory, optional and alternative relationships as well as requires/excludes cross-tree constraints.

## How to Run the Module
___

The module is executed via the command line. Arguments are passed in key/value pairs, with an optional `-slicing` flag.

### Arguments

| Argument          | Description                                          | Required |
|------------------|------------------------------------------------------|----------|
| `-fmFile`        | Path to the input feature model JSON file           | Yes      |
| `-configurations`| Path to the output CSV configuration file          | Yes      |
| `-threshold`     | Optional threshold for slicing (default: 250)      | No       |
| `-slicing`       | Optional flag to enable slicing                     | No       |

### Example Command

Execute this command from the root folder of this project.

```bash
java -cp Configuration-Creator/build/libs/Configuration-Creator-1.0.jar Startup \
 -slicing \
 -fmFile "./TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" \
 -configurations "./TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv"
```

## Description of Key Components
___

### Business Logic
- High-level entry point for reading and slicing feature models, as well as calculating and serializing configurations.

### CNF Clause Generator
- Generates CNF (Conjunctive Normal Form) clauses from feature models (partial or full), encoding feature relations and constraints for SAT-solver processing.

### Configuration Calculator
- Calculates all valid configurations of feature models (partial or full) using CNF clauses and a SAT solver, while handling cross-tree constraints.

### Configuration Serializer
- Serializes and deserializes feature model configurations and constraints to/from a simple CSV format for storage and reuse.

### Feature Model Reader
- Parses feature models from JSON into an internal representation.

### Feature Model Slicer
- Splits feature models into smaller partial models by analyzing connectivity information and configuration complexity.
