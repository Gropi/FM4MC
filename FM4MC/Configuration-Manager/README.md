# ToDo:
Wozu ist das Tool?

## How to Run the Module


### Arguments

| Argument          | Description                                                            | Required |
|-------------------|------------------------------------------------------------------------|----------|
| `-fmFile`         | Path to the input feature model JSON file                              | Yes      |
| `-configurations` | Path to the input CSV configuration file                               | Yes      |
| `-edgeIndex`      | Determines the size/complexity of the edge set in the experiment (1-5) | Yes      |
| `-graph`          | Path to the output graph XML/graphml file                              | Yes      |

### Example Command

```bash
java -cp build/libs/Configuration-Manager-1.0.jar Startup \
  -fmFile "TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" \
  -configurations "TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv" \
  -edgeIndex 1 \
  -graph "TestData/TestGraphs/graph_4.096_EI1.graphml"
