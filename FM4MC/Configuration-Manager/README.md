# Configuration Manager

Merges partial feature model configurations into executable graphs while considering hardware limitations. It removes features unsupported by the target device, builds execution graphs describing dependencies and execution order, and exports these graphs for further orchestration.

## How to Run the Module
___

### Arguments

| Argument | Description | Required |
|-------------------|------------------------------------------------------------------------|----------|
| `-fmFile`         | Path to the input feature model JSON file                              | Yes      |
| `-configurations` | Path to the input CSV configuration file                               | Yes      |
| `-edgeIndex`      | Determines the size/complexity of the edge set in the experiment (1-5) | Yes      |
| `-graph`          | Path to the output graph XML/graphml file                              | Yes      |

### Example Command

Execute this command from the root folder of this project.

```bash
java -jar build/libs/Configuration-Manager-1.0.jar \
  -fmFile "./TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" \
  -configurations "./TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv" \
  -edgeIndex 1 \
  -graph "./TestData/TestGraphs/graph_4.096_EI1.graphml"
```

## Description of Key Components
___

### Business Logic
- High-level entry point for loading feature models and their configurations, gathering hardware information, filtering and graph generation.

### Feature Model Merger
- Merges partial feature model configurations into a unified graph while respecting hardware availability constraints and cross-tree relations (requires/excludes).

### Feature Filter
- Removes features that exceed the hardware capabilities of an edge device, ensuring only suitable features remain.

### Graph Generator
- Builds directed graphs from partial feature model configurations, mapping features to vertices and execution dependencies to edges.

### Graph Serializer
- Exports internal graph structures into XML/GraphML format.
