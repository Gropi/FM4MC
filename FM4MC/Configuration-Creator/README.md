# ToDo: 
Wozu ist das Tool? 
Wie ist der Aufbau der FeatureModel.json (Schema)?
Was sind die Limitationen der aktuellen Parser Funktionalität?


# How to run the module
Input is a FeatureModule (FM) which generates all valid configurations for given FM in CSV.
Run Startup.java with following parameters:

```
            [-path <path>] -> 
            [-file <path>] -> absolut path to FeatureModel (FM)
            [-threshold <int>] -> Maximum size of each sliced FM. Default: 250
            [-slicing] -> Enable FM slicing
            [-destConfigurations <path>] -> absolut path for csv output file
```



Example:
``````
-slicing -file "C:\..\TestData\TestGraphs\TestFMJsons\FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" -destConfigurations "C:\..\TestData\TestGraphs\TestConfigurationFiles\test_config.csv"
``````