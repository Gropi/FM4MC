package GraphGenerator;

import static org.junit.jupiter.api.Assertions.*;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureConnectivityInformation;
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

    @Test
    void generateGraph_multipleConfigurationsWithSameStart_createsAllSequentialEdges() {
        var serviceA = new Feature("A_service", 10, null);
        var featureA = new Feature("A", 1, serviceA);
        var serviceB1 = new Feature("B1_service", 20, null);
        var featureB1 = new Feature("B1", 2, serviceB1);
        var serviceB2 = new Feature("B2_service", 30, null);
        var featureB2 = new Feature("B2", 3, serviceB2);

        var config1 = new PartialConfiguration(List.of(featureA, featureB1));
        var config2 = new PartialConfiguration(List.of(featureA, featureB2));

        featureConnectivityInformation.startFeature = featureA;
        featureConnectivityInformation.featureConnectivityMap = new HashMap<>();

        var graph = graphGenerator.generateGraph(List.of(config1, config2), featureConnectivityInformation);

        var vertexA = graph.getVertexByIdentifier("A");
        var vertexB1 = graph.getVertexByIdentifier("B1");
        var vertexB2 = graph.getVertexByIdentifier("B2");

        assertEquals(1, graph.getEdgesBetweenVertices(vertexA, vertexB1).size());
        assertEquals(1, graph.getEdgesBetweenVertices(vertexA, vertexB2).size());
        assertEquals(3, graph.getAllVertices().size());
    }

    @Test
    void generateGraph_connectivityChain_linksAllConfigurations() {
        var serviceA = new Feature("A_service", 10, null);
        var featureA = new Feature("A", 1, serviceA);
        var serviceB = new Feature("B_service", 20, null);
        var featureB = new Feature("B", 2, serviceB);
        var serviceC = new Feature("C_service", 30, null);
        var featureC = new Feature("C", 3, serviceC);

        var configA = new PartialConfiguration(List.of(featureA));
        var configB = new PartialConfiguration(List.of(featureB));
        var configC = new PartialConfiguration(List.of(featureC));

        featureConnectivityInformation.startFeature = featureA;
        featureConnectivityInformation.featureConnectivityMap = new HashMap<>();
        featureConnectivityInformation.featureConnectivityMap.put("A_service", List.of(featureB));
        featureConnectivityInformation.featureConnectivityMap.put("B_service", List.of(featureC));

        var graph = graphGenerator.generateGraph(List.of(configA, configB, configC), featureConnectivityInformation);

        var vertexA = graph.getVertexByIdentifier("A");
        var vertexB = graph.getVertexByIdentifier("B");
        var vertexC = graph.getVertexByIdentifier("C");

        assertEquals(1, graph.getEdgesBetweenVertices(vertexA, vertexB).size());
        assertEquals(1, graph.getEdgesBetweenVertices(vertexB, vertexC).size());
        assertTrue(graph.getEdgesBetweenVertices(vertexA, vertexC).isEmpty());
    }
}
