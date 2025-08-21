# ToDo: 
Wozu ist das Tool? 
Wie ist der Aufbau der FeatureModel.json (Schema)?
Was sind die Limitationen der aktuellen Parser Funktionalität?

## How to Run the Module

The module is executed via the command line. Arguments are passed in key/value pairs, with an optional `-slicing` flag.

### Arguments

| Argument          | Description                                          | Required |
|------------------|------------------------------------------------------|----------|
| `-fmFile`        | Path to the input feature model JSON file           | Yes      |
| `-configurations`| Path to the output CSV configuration file          | Yes      |
| `-threshold`     | Optional threshold for slicing (default: 250)      | No       |
| `-slicing`       | Optional flag to enable slicing                     | No       |

### Example Command

```bash
java -cp Configuration-Creator/build/libs/Configuration-Creator-1.0.jar Startup \
-slicing \
-fmFile "TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" \
-configurations "TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv"

