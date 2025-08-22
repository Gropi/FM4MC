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

import static io.github.atomfinger.touuid.UUIDs.toUUID;

public class GraphGeneratorV3 {

    private int nextEdgeId = 1;
    private int nextGraphId = 1;
    List<List<IVertex>> vertexConfiguration;

    public Graph generateGraph(List<PartialConfiguration> configuration, FeatureConnectivityInformation featureConnectivityInformation)
    {
        vertexConfiguration = new ArrayList<>();
        var vertices = createVertices(configuration);
        var startVertex = getStartVertex(vertices, featureConnectivityInformation.startFeature);

        var graph = new Graph(nextGraphId, startVertex, "");
        nextGraphId++;

        for (var vertex : vertices) {
            graph.addVertex(vertex);
        }

        generateEdges(graph, configuration, featureConnectivityInformation);

        return graph;
    }

    private List<IVertex> createVertices(List<PartialConfiguration> configuration) {
        var vertices = new ArrayList<IVertex>();
        for (var partialConfiguration : configuration) {
            var partialConfigurationVertices = new ArrayList<IVertex>();
            for (var feature: partialConfiguration.getFeatures()) {
                var vertex = new Vertex(feature.getName(), feature.getIndex(), feature.getParentFeature().getName());
                partialConfigurationVertices.add(vertex);
                vertices.add(vertex);
            }
            vertexConfiguration.add(partialConfigurationVertices);
        }
        return vertices;
    }

    private IVertex getStartVertex(List<IVertex> vertices, Feature startFeature) {
        var startVertex = vertices.stream().filter(x -> x.getLabel().equals(startFeature.getName())).findFirst().orElse(null);
        if (startVertex == null) {
            return new Vertex(startFeature.getName(), startFeature.getIndex(), "startFeature.parentFeature.name");
        }
        return startVertex;
    }

    private UUID createUUIDFromIndex(int index) {
        return toUUID(index);
    }

    private void generateEdges(IGraph graph, List<PartialConfiguration> configuration, FeatureConnectivityInformation featureConnectivityInformation)
    {
        var featuresConnected = vertexConfiguration.stream().map(x -> featureConnectivityInformation.featureConnectivityMap.get(x.get(x.size()-1).getServiceName())).flatMap(Collection::stream).distinct().map(Feature::getName).toList();
        var partialConfigurationsThatStart = vertexConfiguration.stream().filter(x -> !featuresConnected.contains(x.get(0).getServiceName())).toList();
        var partialConfigurationsChecked = new ArrayList<List<IVertex>>();
        var partialConfigurationsUnchecked = new ArrayList<>(partialConfigurationsThatStart);

        while (!partialConfigurationsUnchecked.isEmpty()) {
            var partialConfigurationBeingChecked = partialConfigurationsUnchecked.remove(0);

            for (int i = 1; i < partialConfigurationBeingChecked.size(); i++) {
                var sourceVertex = partialConfigurationBeingChecked.get(i-1);
                var targetVertex = partialConfigurationBeingChecked.get(i);
                graph.addEdge(new Edge(sourceVertex, targetVertex, nextEdgeId));
                nextEdgeId++;
            }

            var endVertexOfPartialConfiguration = partialConfigurationBeingChecked.get(partialConfigurationBeingChecked.size()-1);
            var followingAbstractFeatures = featureConnectivityInformation.featureConnectivityMap.get(endVertexOfPartialConfiguration.getServiceName()).stream().map(Feature::getName).toList();
            var followingPartialConfigurations = vertexConfiguration.stream().filter(x -> followingAbstractFeatures.contains(x.get(0).getServiceName())).toList();

            partialConfigurationsChecked.add(partialConfigurationBeingChecked);

            for (var partialConfiguration : followingPartialConfigurations) {
                if (!partialConfigurationsUnchecked.contains(partialConfiguration) && !partialConfigurationsChecked.contains(partialConfiguration)) {
                    partialConfigurationsUnchecked.add(partialConfiguration);
                }
                var startVertexOfPartialConfiguration = partialConfiguration.get(0);
                graph.addEdge(new Edge(endVertexOfPartialConfiguration, startVertexOfPartialConfiguration, nextEdgeId));
                nextEdgeId++;
            }
        }
    }

    public void recalculateIndices(IGraph graph) {
        var vertices = graph.getAllVertices();
        var stage = 0;
        var verticesPerStage = vertices.stream().filter(x -> x.getStage() == 0).toList();
        while(!verticesPerStage.isEmpty()) {
            var distinctApplications = verticesPerStage.stream().map(IVertex::getServiceName).distinct().toList();
            var applicationIndex = 0;
            for (var application : distinctApplications) {
                var approximationIndex = 0;
                var applicationVertices = verticesPerStage.stream().filter(x -> Objects.equals(x.getServiceName(), application)).toList();

                for (var vertex : applicationVertices) {
                    vertex.setApplicationIndex(applicationIndex);
                    vertex.setApproximationIndex(approximationIndex);
                    approximationIndex++;
                }
                applicationIndex++;
            }
            stage++;
            int finalStage = stage;
            verticesPerStage = vertices.stream().filter(x -> x.getStage() == finalStage).toList();
        }
    }

    private IVertex getVertexForFeature(IGraph graph, Feature feature) {
        return graph.getVertexByIdentifier(feature.getName());
    }
}
