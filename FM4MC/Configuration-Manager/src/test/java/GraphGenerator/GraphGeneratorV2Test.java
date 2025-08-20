package test.GraphGenerator;

import static org.junit.jupiter.api.Assertions.*;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureConnectivityInformation;
import GraphGenerator.GraphGeneratorV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class GraphGeneratorV2Test {

    private GraphGeneratorV2 graphGenerator;
    private FeatureConnectivityInformation featureConnectivityInformation;

    @BeforeEach
    void setUp() {
        graphGenerator = new GraphGeneratorV2();
        featureConnectivityInformation = new FeatureConnectivityInformation();
    }

    @Test
    void generateGraph_withNoConfiguration_returnsStartOnly() {
        var startFeature = new Feature("Start", 1, null);
        featureConnectivityInformation.startFeature = startFeature;
        featureConnectivityInformation.featureConnectivityMap = new HashMap<>();

        var graph = graphGenerator.generateGraph(Collections.emptyList(), featureConnectivityInformation);

        assertNotNull(graph);
        assertEquals(1, graph.getAllVertices().size());
        assertTrue(graph.getAllEdges().isEmpty());
        assertEquals("Start", graph.getStart().getLabel());
    }

    @Test
    void generateGraph_singleConfiguration_createsSequentialEdges() {
        var serviceA = new Feature("A_service", 10, null);
        var featureA = new Feature("A", 1, serviceA);
        var serviceB = new Feature("B_service", 20, null);
        var featureB = new Feature("B", 2, serviceB);
        var serviceC = new Feature("C_service", 30, null);
        var featureC = new Feature("C", 3, serviceC);

        var config = new PartialConfiguration(List.of(featureA, featureB, featureC));

        featureConnectivityInformation.startFeature = featureA;
        featureConnectivityInformation.featureConnectivityMap = new HashMap<>();

        var graph = graphGenerator.generateGraph(List.of(config), featureConnectivityInformation);

        assertEquals(3, graph.getAllVertices().size());
        var vertexA = graph.getVertexByIdentifier("A");
        var vertexB = graph.getVertexByIdentifier("B");
        var vertexC = graph.getVertexByIdentifier("C");

        assertEquals(1, graph.getEdgesBetweenVertices(vertexA, vertexB).size());
        assertEquals(1, graph.getEdgesBetweenVertices(vertexB, vertexC).size());
        assertTrue(graph.getEdgesBetweenVertices(vertexA, vertexC).isEmpty());
    }

    @Test
    void generateGraph_connectivityLinksConfigurations() {
        var serviceA1 = new Feature("A1_service", 10, null);
        var featureA1 = new Feature("A1", 1, serviceA1);
        var serviceA2 = new Feature("A2_service", 20, null);
        var featureA2 = new Feature("A2", 2, serviceA2);
        var serviceB = new Feature("B_service", 30, null);
        var featureB = new Feature("B", 3, serviceB);

        var config1 = new PartialConfiguration(List.of(featureA1));
        var config2 = new PartialConfiguration(List.of(featureA2));
        var config3 = new PartialConfiguration(List.of(featureB));

        featureConnectivityInformation.startFeature = featureA1;
        featureConnectivityInformation.featureConnectivityMap = new HashMap<>();
        featureConnectivityInformation.featureConnectivityMap.put("A1_service", List.of(featureB));
        featureConnectivityInformation.featureConnectivityMap.put("A2_service", List.of(featureB));

        var graph = graphGenerator.generateGraph(List.of(config1, config2, config3), featureConnectivityInformation);

        var vertexA1 = graph.getVertexByIdentifier("A1");
        var vertexA2 = graph.getVertexByIdentifier("A2");
        var vertexB = graph.getVertexByIdentifier("B");

        assertEquals(1, graph.getEdgesBetweenVertices(vertexA1, vertexB).size());
        assertEquals(1, graph.getEdgesBetweenVertices(vertexA2, vertexB).size());
        assertEquals(0, graph.getEdgesBetweenVertices(vertexA1, vertexA2).size());
        assertEquals(3, graph.getAllVertices().size());
    }
}
