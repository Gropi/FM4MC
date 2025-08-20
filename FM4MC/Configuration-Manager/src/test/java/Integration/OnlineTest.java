package integration;

import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationSerializer.ConfigurationSerializer;
import CreatorTestData.TestGraphCreator;
import DecisionMaking.MobiDic.MobiDiCManager;
import FeatureModelMerger.HardwareSensitiveFeatureModelMerger;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelSlicer.FeatureModelSlicer;
import IO.impl.LshwClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnlineTest {
    private final Logger _Logger = LogManager.getLogger("executionLog");
    private AvailableEdgeHardware _EdgeInformation = null;
    private FeatureModelPartiallyCalculated _SmallFeatureModelWithConfigurations = null;
    private FeatureModelPartiallyCalculated _HugeFeatureModelWithConfigurations = null;
    private final FeatureModelReader _FMReader = new FeatureModelReader(_Logger);
    private final ConfigurationSerializer _ConfigurationSerializer = new ConfigurationSerializer(_Logger);

    @BeforeEach()
    public void initTests() throws InvalidFeatureModelRelationException {
        var smallCurrentFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json");
        var hugeCurrentFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json");
        var smallFilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv";
        var hugeFilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json_10.csv";

        //Setting up the edges
        //Only the distinct set values for (DISPLAY, PROCESSOR, SYSTEM) have an effect on
        //the amount of valid configurations. Everything else is just there to create workload in checking requirements
        var countrySideEdge = new AvailableEdgeHardware(2);

        countrySideEdge.edgeHardware.put(LshwClass.DISPLAY, 1);
        countrySideEdge.edgeHardware.put(LshwClass.PROCESSOR, 3);
        countrySideEdge.edgeHardware.put(LshwClass.MEMORY, 2);

        _EdgeInformation = countrySideEdge;

        _SmallFeatureModelWithConfigurations = _ConfigurationSerializer.loadConfigurations(_FMReader.readFeatureModelJson(smallCurrentFile), smallFilePathConfiguration);
        _HugeFeatureModelWithConfigurations = _ConfigurationSerializer.loadConfigurations(_FMReader.readFeatureModelJson(hugeCurrentFile), hugeFilePathConfiguration);
    }

    @Test
    public void onlinePhaseBenchmarkMobiDic() {
        var hardwareSensitiveMerger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var graph = hardwareSensitiveMerger.startForTesting(_SmallFeatureModelWithConfigurations, _EdgeInformation, 2);

        var testGraphCreator = new TestGraphCreator(_Logger);
        var randomizedGraph = testGraphCreator.randomizeGraphCostWithAdvancedParameters(graph);

        var decisionMaker = new MobiDiCManager(randomizedGraph);
    }

    @Test
    void testSlicingSimpleChain() throws Exception {
        var readFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json");
        var reader = new FeatureModelReader(_Logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicer = new FeatureModelSlicer(_Logger);
        int threshold = 10;
        var slicedFM = slicer.sliceFeatureModel(model, threshold);
        var slicedModels = slicedFM.partialConcreteFeatureModels;

        var configCalc = new ConfigurationCalculator(_Logger);
        //var partialFM = configCalc.calculateConfigurations(slicedFM);
        var finalFM = configCalc.calculatePartialConfigurations(slicedFM);

        var serializer = new ConfigurationSerializer(_Logger);
        serializer.saveConfigurations(finalFM, "temp.csv");
        var newFM = serializer.loadConfigurations(model, "temp.csv");

        var countrySideEdge = new AvailableEdgeHardware(2);

        countrySideEdge.edgeHardware.put(LshwClass.DISPLAY, 1);
        countrySideEdge.edgeHardware.put(LshwClass.PROCESSOR, 3);
        countrySideEdge.edgeHardware.put(LshwClass.MEMORY, 2);

        var map = new HashMap<>(newFM.featureConnectivityInformation.featureConnectivityMap);

        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var res = merger.startForTesting(newFM, countrySideEdge, 2);


        // Expect one slice containing startTask, task1, task2.
        assertEquals(1, slicedModels.size(), "There should be one slice for a simple chain");
        var slice = slicedModels.get(0);
        assertEquals(3, slice.size(), "Slice should contain 3 features");
        assertEquals("startTask", slice.get(0).getName());
        assertEquals("task1", slice.get(1).getName());
        assertEquals("task2", slice.get(2).getName());
    }

    @Test
    public void testSmallFM() {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var graph = merger.startForTesting(_SmallFeatureModelWithConfigurations, _EdgeInformation, 12);
    }

    @Test
    public void testHugeFMSmallEdge() {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var graph = merger.startForTesting(_HugeFeatureModelWithConfigurations, _EdgeInformation, 2);
    }

    @Test
    public void testHugeFMFullEdge() {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var fullEdge = new AvailableEdgeHardware(10);
        var graph = merger.startForTesting(_HugeFeatureModelWithConfigurations, fullEdge, 12);
    }
}
