package GraphGenerator;

import static org.junit.jupiter.api.Assertions.*;

import CNFClauseGenerator.CNFClauseGenerator;
import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationSerializer.ConfigurationSerializer;
import CreatorTestData.TestGraphCreator;
import FeatureModelMerger.HardwareSensitiveFeatureModelMerger;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelReader.FeatureModelReader;
import FeatureModelSlicer.FeatureModelSlicer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import FeatureModelReader.InvalidFeatureModelRelationException;

import java.io.File;
import java.util.*;

import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureConnectivityInformation;

class GraphGeneratorV2Test {
    private final Logger _Logger = LogManager.getLogger("executionLog");

    private GraphGeneratorV2 graphGenerator;
    private FeatureConnectivityInformation featureConnectivityInformation;
    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);
    private final FeatureModelSlicer fmSlicer = new FeatureModelSlicer(_Logger);
    private final CNFClauseGenerator cnfClauseGenerator = new CNFClauseGenerator(_Logger);
    private final ConfigurationCalculator configurationCalculator = new ConfigurationCalculator(_Logger);
    private final ConfigurationSerializer fmSerializer = new ConfigurationSerializer(_Logger);
    private final HardwareSensitiveFeatureModelMerger hardwareSensitiveFeatureModelMerger = new HardwareSensitiveFeatureModelMerger(_Logger);

    @BeforeEach
    void setUp() {
        graphGenerator = new GraphGeneratorV2();
        featureConnectivityInformation = new FeatureConnectivityInformation();
    }

    @Test
    void testGenerateGraph_withValidConfiguration_FromPreprocessed() throws InvalidFeatureModelRelationException {
        var readFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json");
        var _FilePathConfiguration = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json_10.csv";
        var readFeatureModel = fmReader.readFeatureModelJson(readFile);

        var fullEdge = new AvailableEdgeHardware(10);

        var calculatedConfigurations = fmSerializer.loadConfigurations(readFeatureModel, _FilePathConfiguration);

        var graph = hardwareSensitiveFeatureModelMerger.startForTesting(calculatedConfigurations, fullEdge, 14);

        var testGraphCreator = new TestGraphCreator(_Logger);
        var randomizedGraph = testGraphCreator.randomizeGraphCostWithAdvancedParameters(graph);
    }

    @Test
    void testGenerateGraph_withValidConfiguration_shouldCreateGraphWithVerticesAndEdges() throws InvalidFeatureModelRelationException {
        // Arrange
        var fullEdge = new AvailableEdgeHardware(10);

        var readFile = fmReader.readFeatureModelJson(new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json"));
        var slicedFeature = fmSlicer.sliceFeatureModel(readFile, 250);
        var calculatedConfigurations = configurationCalculator.calculatePartialConfigurations(slicedFeature);

        // Act
        var graph = hardwareSensitiveFeatureModelMerger.startForTesting(calculatedConfigurations, fullEdge, 1);

        var testGraphCreator = new TestGraphCreator(_Logger);
        var randomizedGraph = testGraphCreator.randomizeGraphCostWithAdvancedParameters(graph);
    }

    @Test
    void testGenerateGraph_withNoConfiguration_shouldReturnEmptyGraph() {
        // Arrange
        var startFeature = new Feature("Start", 1, null);
        featureConnectivityInformation.startFeature = startFeature;
        featureConnectivityInformation.featureConnectivityMap = new HashMap<>();

        // Act
        var graph = graphGenerator.generateGraph(Collections.emptyList(), featureConnectivityInformation);

        // Assert
        assertNotNull(graph);
        assertEquals(1, graph.getAllVertices().size());
        assertTrue(graph.getAllEdges().isEmpty());
    }
}
