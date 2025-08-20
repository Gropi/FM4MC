package GraphGenerator;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureConnectivityInformation;
import FeatureModelReader.Structures.FeatureModelRead;
import Structures.Graph.Edge;
import Structures.Graph.Graph;
import Structures.Graph.Vertex;
import Structures.Graph.interfaces.IVertex;
import Structures.IGraph;
import com.github.f4b6a3.uuid.UuidCreator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.atomfinger.touuid.UUIDs.toUUID;


/**
 * Alternative graph generator implementation supporting single and multiple
 * configurations. Vertices correspond to features while edges capture their
 * execution order and connectivity.
 */
public class GraphGeneratorV2 {

    private AtomicInteger nextEdgeId = new AtomicInteger(1); // Thread-safe counter for edge IDs
    private Map<String, List<IVertex>> vertexConfigurationMap; // Map for vertex configurations

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

        var graph = new Graph(new Random().nextInt(10000), startVertex, "");

        // Add vertices to the graph
        vertices.forEach(graph::addVertex);

        // Generate edges
        generateEdges(graph, featureConnectivityInformation);

        return graph;
    }

    /**
     * Builds a graph from a single partial configuration.
     *
     * @param configuration the configuration to convert
     * @param featureConnectivityInformation connectivity details of the model
     * @param fm complete feature model used for indexing
     * @return constructed graph
     */
    public Graph generateGraphFromSingleConfiguration(PartialConfiguration configuration, FeatureConnectivityInformation featureConnectivityInformation, FeatureModelRead fm) {
        vertexConfigurationMap = new HashMap<>();
        var vertices = createVertices(Collections.singletonList(configuration));
        var startVertex = getStartVertex(vertices, featureConnectivityInformation.startFeature);

        var graph = new Graph(new Random().nextInt(10000), startVertex, "");

        // Add vertices to the graph
        vertices.forEach(graph::addVertex);

        // Create a map for quick vertex lookup
        Map<String, IVertex> vertexMap = vertices.stream()
                .collect(Collectors.toMap(IVertex::getServiceName, v -> v));

        // Add edges based on feature connectivity
        vertices.forEach(vertex -> {
            var connectedFeatures = featureConnectivityInformation.featureConnectivityMap.get(vertex.getServiceName());
            if (connectedFeatures != null) {
                connectedFeatures.forEach(feature -> {
                    var connectedVertex = vertexMap.get(feature.getName());
                    if (connectedVertex != null) {
                        graph.addEdge(new Edge(vertex, connectedVertex, nextEdgeId.getAndIncrement()));
                    }
                });
            }
        });

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
            vertexConfigurationMap.put(config.getFeatures().get(0).getName(), configVertices);
        }
        return vertices;
    }

    private IVertex getStartVertex(List<IVertex> vertices, Feature startFeature) {
        return vertices.stream()
                .filter(x -> x.getLabel().equals(startFeature.getName()))
                .findFirst()
                .orElseGet(() -> new Vertex(startFeature.getName(), startFeature.getIndex(), "startFeature.parentFeature.name"));
    }

    private void generateEdges(IGraph graph, FeatureConnectivityInformation featureConnectivityInformation) {
        vertexConfigurationMap.values().forEach(config -> {
            for (int i = 1; i < config.size(); i++) {
                var sourceVertex = config.get(i - 1);
                var targetVertex = config.get(i);
                graph.addEdge(new Edge(sourceVertex, targetVertex, nextEdgeId.getAndIncrement()));
            }

            var endVertex = config.get(config.size() - 1);
            var connectedFeatures = featureConnectivityInformation.featureConnectivityMap.get(endVertex.getServiceName());

            if (connectedFeatures != null) {
                connectedFeatures.forEach(feature -> {
                    var followingConfig = vertexConfigurationMap.get(feature.getName());
                    if (followingConfig != null) {
                        var startVertex = followingConfig.get(0);
                        graph.addEdge(new Edge(endVertex, startVertex, nextEdgeId.getAndIncrement()));
                    }
                });
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
