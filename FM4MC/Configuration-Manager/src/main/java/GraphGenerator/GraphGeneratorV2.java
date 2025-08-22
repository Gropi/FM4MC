package GraphGenerator;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureConnectivityInformation;
import Structures.Graph.Edge;
import Structures.Graph.Graph;
import Structures.Graph.Vertex;
import Structures.Graph.interfaces.IVertex;
import Structures.IGraph;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Alternative graph generator implementation supporting single and multiple
 * configurations. Vertices correspond to features while edges capture their
 * execution order and connectivity.
 */
public class GraphGeneratorV2 {

    private final AtomicInteger nextEdgeId = new AtomicInteger(1); // Thread-safe counter for edge IDs
    private final AtomicInteger nextGraphId = new AtomicInteger(1);
    // Multiple partial configurations can start with the same feature. Store all
    // configurations for a feature to avoid losing edges when duplicates occur.
    private Map<String, List<List<IVertex>>> vertexConfigurationMap; // Map for vertex configurations

    /**
     * Generates a graph for the given set of partial configurations.
     *
     * @param configuration list of partial configurations to transform
     * @param featureConnectivityInformation connectivity information of features
     * @return constructed graph
     */
    public Graph generateGraph(List<PartialConfiguration> configuration, FeatureConnectivityInformation featureConnectivityInformation) {
        vertexConfigurationMap = new HashMap<>();
        var vertices = createVertices(configuration);
        var startVertex = getStartVertex(vertices, featureConnectivityInformation.startFeature);

        var graph = new Graph(nextGraphId.getAndIncrement(), startVertex, "");

        // Add vertices to the graph
        vertices.forEach(graph::addVertex);

        // Generate edges
        generateEdges(graph, configuration, featureConnectivityInformation);

        return graph;
    }

    private List<IVertex> createVertices(List<PartialConfiguration> configurations) {
        var vertices = new ArrayList<IVertex>();
        for (var config : configurations) {
            var configVertices = new ArrayList<IVertex>();
            for (var feature : config.getFeatures()) {
                var vertex = new Vertex(feature.getName(), feature.getIndex(), feature.getParentFeature().getName());
                configVertices.add(vertex);
                vertices.add(vertex);
            }
            // Add the list of vertices for this partial configuration under its
            // starting feature. Multiple configurations may share the same start
            // feature, therefore we keep a list for each key.
            var startFeatureName = config.getFeatures().get(0).getName();
            vertexConfigurationMap
                    .computeIfAbsent(startFeatureName, k -> new ArrayList<>())
                    .add(configVertices);
        }
        return vertices;
    }

    private IVertex getStartVertex(List<IVertex> vertices, Feature startFeature) {
        return vertices.stream()
                .filter(x -> x.getLabel().equals(startFeature.getName()))
                .findFirst()
                .orElseGet(() -> new Vertex(startFeature.getName(), startFeature.getIndex(), "startFeature.parentFeature.name"));
    }

    private void generateEdges(IGraph graph, List<PartialConfiguration> configuration, FeatureConnectivityInformation featureConnectivityInformation) {
        vertexConfigurationMap.values().forEach(configList -> {
            for (var config : configList) {
                for (int i = 1; i < config.size(); i++) {
                    var sourceVertex = config.get(i - 1);
                    var targetVertex = config.get(i);
                    graph.addEdge(new Edge(sourceVertex, targetVertex, nextEdgeId.getAndIncrement()));
                }

                var endVertex = config.get(config.size() - 1);
                var connectedFeatures = featureConnectivityInformation.featureConnectivityMap.get(endVertex.getServiceName());

                if (connectedFeatures != null) {
                    connectedFeatures.forEach(feature -> {
                        var followingConfigs = vertexConfigurationMap.get(feature.getName());
                        if (followingConfigs != null) {
                            for (var followingConfig : followingConfigs) {
                                var startVertex = followingConfig.get(0);
                                graph.addEdge(new Edge(endVertex, startVertex, nextEdgeId.getAndIncrement()));
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Recomputes stage, application, and approximation indices for all vertices
     * in the given graph.
     *
     * @param graph graph whose vertex indices should be recalculated
     */
    public void recalculateIndices(IGraph graph) {
        var vertices = graph.getAllVertices();
        int stage = 0;

        while (true) {
            int finalStage = stage;
            var verticesPerStage = vertices.stream()
                    .filter(v -> v.getStage() == finalStage)
                    .toList();

            if (verticesPerStage.isEmpty()) {
                break;
            }

            var distinctApplications = verticesPerStage.stream()
                    .map(IVertex::getServiceName)
                    .distinct()
                    .toList();

            int applicationIndex = 0;
            for (var application : distinctApplications) {
                int approximationIndex = 0;
                for (var vertex : verticesPerStage) {
                    if (vertex.getServiceName().equals(application)) {
                        vertex.setApplicationIndex(applicationIndex);
                        vertex.setApproximationIndex(approximationIndex++);
                    }
                }
                applicationIndex++;
            }
            stage++;
        }
    }
}
