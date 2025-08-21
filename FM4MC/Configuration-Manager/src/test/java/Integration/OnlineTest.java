package Integration;

import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelMerger.HardwareSensitiveFeatureModelMerger;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelSlicer.FeatureModelSlicer;
import IO.impl.LshwClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requires large datasets and external dependencies")
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
    void testSlicingBenchmarkGraph() throws Exception {
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


        // The benchmark graph is sliced into individual tasks plus a final pair (task7, endTask).
        assertEquals(12, slicedModels.size(), "Unexpected number of slices for benchmark graph");
        assertEquals(1, slicedModels.get(0).size(), "First slice should contain only startTask");
        assertEquals("startTask", slicedModels.get(0).getFirst().getName());
        assertEquals("task1", slicedModels.get(1).getFirst().getName());
        var terminalSlice = slicedModels.get(3);
        assertEquals(2, terminalSlice.size(), "Fourth slice should contain two terminal tasks");
        assertEquals("task7", terminalSlice.get(0).getName());
        assertEquals("endTask", terminalSlice.get(1).getName());
    }

    @Test
    public void testSmallFM() {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var graph = merger.startForTesting(_SmallFeatureModelWithConfigurations, _EdgeInformation, 12);
        assertNotNull(graph, "Graph should not be null for small feature model");
        assertTrue(merger.validConfigurations > 0, "Should merge at least one valid configuration");
    }

    @Test
    public void testHugeFMSmallEdge() {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var graph = merger.startForTesting(_HugeFeatureModelWithConfigurations, _EdgeInformation, 2);
        assertNotNull(graph, "Graph should not be null for huge feature model with limited edge");
        assertTrue(merger.validConfigurations > 0, "Should merge at least one valid configuration");
    }

    @Test
    public void testHugeFMFullEdge() {
        var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
        var fullEdge = new AvailableEdgeHardware(10);
        var graph = merger.startForTesting(_HugeFeatureModelWithConfigurations, fullEdge, 12);
        assertNotNull(graph, "Graph should not be null for huge feature model with full edge");
        assertTrue(merger.validConfigurations > 0, "Should merge at least one valid configuration");
    }
}
