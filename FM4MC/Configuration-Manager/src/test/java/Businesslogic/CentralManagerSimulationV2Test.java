package Businesslogic;

import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelMerger.HardwareSensitiveFeatureModelMerger;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import GraphGenerator.GraphGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class CentralManagerSimulationV2Test {
    private final Logger _Logger = LogManager.getLogger("executionLog");

    @Test
    void generateGraph_withGraphGeneratorV2_matchesExpectedGraph() throws InvalidFeatureModelRelationException {
        var fmReader = new FeatureModelReader(_Logger);
        var fmSerializer = new ConfigurationSerializer(_Logger);

        var configurationPath = "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv";
        var fmPath = "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json";

        var featureModelWithConfigurations = fmSerializer.loadConfigurations(fmReader.readFeatureModelJson(new File(fmPath)), configurationPath);

        var edgeInformation = new AvailableEdgeHardware(10);

        var merger = new HardwareSensitiveFeatureModelMerger(_Logger, new GraphGenerator());
        var graph = merger.startForTesting(featureModelWithConfigurations, edgeInformation, 14);
        graph.recalculateGraphStages();

        assertNotNull(graph);
        assertEquals(27, graph.getAllVertices().size());
        assertEquals(36, graph.getAllEdges().size());
        assertEquals("tsv1", graph.getStart().getLabel());
        assertEquals("tev1", graph.getEnd().getLabel());
    }
}
