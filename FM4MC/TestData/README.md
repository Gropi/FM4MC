# TestData

This directory contains sample inputs used for experiments and benchmarks.

## EdgeNodeJsons
JSON descriptions of edge node configurations used in the paper:

- **EdgeNodes_CountrySide.json** – edge nodes in a countryside scenario.
- **EdgeNodes_Full.json** – comprehensive set combining all nodes.
- **EdgeNodes_Highway.json** – nodes placed along a highway.
- **EdgeNodes_MediumCity.json** – nodes representing a medium-sized city.
- **EdgeNodes_SmallCity.json** – nodes representing a small city.

## TestGraphs
Microservice-chain feature models and example configuration outputs.

### TestFMJsons
Feature models in JSON format. The filename suffix indicates the number of valid configurations.

- **FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json** – "Tiny" model with 4,096 valid configurations.
- **FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json** – variant with 57,344 valid configurations.
- **FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json** – "Medium" model with 139,968 valid configurations.
- **FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json** – "Big" model with 1,520,640 valid configurations.
- **FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json** – "Huge" model with 14,348,907 valid configurations.

These models correspond to the feature models referenced in our paper. The following table provides the mapping between the names used in the paper and their structural properties:

| Name | Valid Configurations | Stages | Alternatives | Parallel Tasks | Services |
|------|---------------------:|-------:|-------------:|---------------:|---------:|
| Tiny  | 4,096      | 5  | 4     | 5     | 9  |
| Small | 37,120     | 6  | 2–4   | 4–7   | 12 |
| Medium| 139,968    | 8  | 2–3   | 2–6   | 20 |
| Big   | 1,520,640  | 10 | 2–5   | 4–6   | 18 |
| Huge  | 14,348,907 | 10 | 3     | 4–6   | 18 |

### TestConfigurationFiles
Example CSV files containing valid configurations derived from the feature models:

- **temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv** – configurations for the Tiny model.
- **temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json_10.csv** – configurations for the 57,344-config variant.
- **temporaryConfigurationFile_FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json_10.csv** – configurations for the Medium model.
- **temporaryConfigurationFile_FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json_10.csv** – configurations for the Big model.
- **temporaryConfigurationFile_FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json_10.csv** – configurations for the Huge model.

## JMH_Online_Phase_Benchmark_Additional_Information
Additional data for the online-phase JMH benchmark:

- **JMH_Online_Phase_Benchmark_Information.csv** – benchmark input details.

