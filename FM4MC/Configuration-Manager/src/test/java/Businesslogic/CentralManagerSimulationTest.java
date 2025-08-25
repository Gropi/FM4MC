package Businesslogic;

import Graph.GraphOnlineParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CentralManagerSimulationTest {
    private final Logger _Logger = LogManager.getLogger("executionLog");

    @Test
    void generateGraph_withNoConfiguration_returnsStartOnly() {
        var instanceUnderTest = new CentralManagerSimulation(_Logger);

        instanceUnderTest.startOnlinePhase(
                "../TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv",
                "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json",
                5, "temp.graphml");

        var graph = new GraphOnlineParser(_Logger).loadBaseGraph("temp.graphml", 1);

        assertNotNull(graph);
        assertEquals(27, graph.getAllVertices().size());
        assertEquals(36, graph.getAllEdges().size());
        assertEquals("tsv1", graph.getStart().getLabel());
        assertEquals("tev1", graph.getEnd().getLabel());
    }
}
