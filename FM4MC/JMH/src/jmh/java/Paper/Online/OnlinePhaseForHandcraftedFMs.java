package Paper.Online;

import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelMerger.HardwareSensitiveFeatureModelMerger;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.FeatureModelRead;
import IO.impl.LshwClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1)
public class OnlinePhaseForHandcraftedFMs {

    private final Logger _Logger = LogManager.getLogger("executionLog");

    // Params
    @Param({
            "FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json",
            "FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json",
            "FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json",
            "FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json"
    })
    public String _FilePathFM;

    @Param({"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"})
    public int _MaxRequirements;

    @Param({"5", "4", "3", "2", "1"})
    public int _EdgeIndex;

    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);
    private final ConfigurationSerializer fmSerializer = new ConfigurationSerializer(_Logger);
    private FeatureModelRead _ReadFeatureModel = null;
    private FeatureModelPartiallyCalculated _FeatureModelWithConfigurations = null;
    private String _FilePathConfiguration = null;
    private AvailableEdgeHardware _EdgeInformation = null;

    @Setup(Level.Trial)
    public void initTests() throws InvalidFeatureModelRelationException {
        var currentFile = new File(_FilePathFM);
        _ReadFeatureModel = fmReader.readFeatureModelJson(currentFile);

        switch (currentFile.getName()) {
            case "FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" -> _FilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv";
            case "FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json" -> _FilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json_10.csv";
            case "FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json" -> _FilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json_10.csv";
            case "FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json" -> _FilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json_10.csv";
            case "FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json" -> _FilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json_10.csv";
        }

        //Setting up the edges
        //Only the distinct set values for (DISPLAY, PROCESSOR, SYSTEM) have an effect on
        //the amount of valid configurations. Everything else is just there to create workload in checking requirements
        var countrySideEdge = new AvailableEdgeHardware(2);
        var smallCityEdge = new AvailableEdgeHardware(4);
        var highwayEdge = new AvailableEdgeHardware(6);
        var mediumCityEdge = new AvailableEdgeHardware(8);
        var fullEdge = new AvailableEdgeHardware(10);

        countrySideEdge.edgeHardware.put(LshwClass.DISPLAY, 1);
        countrySideEdge.edgeHardware.put(LshwClass.PROCESSOR, 3);
        countrySideEdge.edgeHardware.put(LshwClass.MEMORY, 2);

        smallCityEdge.edgeHardware.put(LshwClass.DISPLAY, 10);
        smallCityEdge.edgeHardware.put(LshwClass.PROCESSOR, 6);
        smallCityEdge.edgeHardware.put(LshwClass.MEMORY, 5);

        highwayEdge.edgeHardware.put(LshwClass.DISPLAY, 10);
        highwayEdge.edgeHardware.put(LshwClass.PROCESSOR, 6);
        highwayEdge.edgeHardware.put(LshwClass.MEMORY, 5);

        mediumCityEdge.edgeHardware.put(LshwClass.DISPLAY, 10);
        mediumCityEdge.edgeHardware.put(LshwClass.PROCESSOR, 9);
        mediumCityEdge.edgeHardware.put(LshwClass.MEMORY, 8);

        switch (_EdgeIndex) {
            case 1 -> _EdgeInformation = countrySideEdge;
            case 2 -> _EdgeInformation = smallCityEdge;
            case 3 -> _EdgeInformation = highwayEdge;
            case 4 -> _EdgeInformation = mediumCityEdge;
            case 5 -> _EdgeInformation = fullEdge;
        }

        _FeatureModelWithConfigurations = fmSerializer.loadConfigurations(_ReadFeatureModel, _FilePathConfiguration);
    }

    private void saveBaseInformation(File featureModelFile, HardwareSensitiveFeatureModelMerger merger) {
        //Here we create and save the additional information for each benchmark
        var dateTime = new Date();

        //General information
        var sbAdditionalInfo = new StringBuilder();
        var _Delimiter = ";";
        if (_MaxRequirements == 1 && _EdgeIndex == 1 && featureModelFile.getName().equals("FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json")) {
            sbAdditionalInfo.append("timestamp").append(_Delimiter).append("File").append(_Delimiter).append("edgeIndex")
                    .append(_Delimiter).append("maxRequirements").append(_Delimiter).append("validConfigurations").append("\r\n");
        }
        sbAdditionalInfo.append(dateTime).append(_Delimiter)
                .append(featureModelFile.getName()).append(_Delimiter)
                .append(_EdgeIndex).append(_Delimiter)
                .append(_MaxRequirements).append(_Delimiter)
                .append(merger.validConfigurations).append(_Delimiter).append("\r\n");

        //Save additional information
        try {
            var basePath = "../TestData/JMH_Online_Phase_Benchmark_Additional_Information";
            Files.createDirectories(Paths.get(basePath));
            var additionalInformationFileOutput = new File(basePath, "JMH_Online_Phase_Benchmark_Information.csv");
            var bw = new BufferedWriter(new FileWriter(additionalInformationFileOutput, true));
            bw.write(sbAdditionalInfo.toString());
            bw.close();
        } catch (Exception e) {
            _Logger.fatal(e);
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.SingleShotTime})
    @Warmup(iterations = 3)
    @Measurement(iterations = 20)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void onlinePhaseBenchmark(Blackhole blackhole) {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var graph = merger.startForTesting(_FeatureModelWithConfigurations, _EdgeInformation, _MaxRequirements);

        blackhole.consume(graph);
        blackhole.consume(merger);
    }

    @Benchmark
    @BenchmarkMode({Mode.SingleShotTime})
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void onlinePhaseBenchmarkLessIterations(Blackhole blackhole) {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var graph = merger.startForTesting(_FeatureModelWithConfigurations, _EdgeInformation, _MaxRequirements);

        blackhole.consume(graph);
        blackhole.consume(merger);
    }
}
