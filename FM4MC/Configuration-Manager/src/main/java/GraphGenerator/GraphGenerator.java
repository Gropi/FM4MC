package GraphGenerator;

import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import FeatureModelReader.Structures.Feature;
import Structures.Graph.Edge;
import Structures.Graph.Graph;
import Structures.Graph.Vertex;
import Structures.Graph.interfaces.IVertex;
import Structures.IGraph;

import java.util.*;

import static io.github.atomfinger.touuid.UUIDs.toUUID;
import static java.util.stream.Collectors.groupingBy;

public class GraphGenerator {

    private FeatureModelPartiallyCalculated _FeatureModel;
    private int _NextEdgeID = 1;

    public IGraph generateGraph(FeatureModelPartiallyCalculated featureModel) {
        var partialConfigurations = featureModel.configurationsPerPartialFeatureModel.stream().flatMap(List::stream).toList();

        _FeatureModel = featureModel;
        var vertices = new ArrayList<IVertex>();
        var nextVertexId = 1;

        for (var configuration : partialConfigurations) {
            var newFeatures = configuration.getFeatures().stream().filter(x -> !vertices.stream().map(IVertex::getLabel).toList().contains(x.getName())).toList();

            for (var feature : newFeatures) {
                var vertex = new Vertex(feature.getName(), nextVertexId, "mockService");
                var parentFeature = featureModel.features.stream().filter(x -> x.getName().equals(feature.getParentFeatureName())).findFirst().orElseThrow();
                vertex.setApplicationIndex(parentFeature.getIndex());
                nextVertexId++;

                vertices.add(vertex);
            }
        }

        var startFeature = findStartFeature(featureModel.featureConnectivityInformation.featureConnectivityMap, featureModel.features);
        var childOfStartFeature = getChildFeatures(startFeature).get(0);
        var startVertex = vertices.stream().filter(x -> x.getLabel().equals(childOfStartFeature.getName())).findFirst().orElseThrow();
        vertices.remove(startVertex);

        var resultGraph = addVerticesToGraph(new Graph(new Random().nextInt(10000), startVertex, ""), vertices);

        recursiveAddEdges(resultGraph, startFeature, new ArrayList<>());
        resultGraph.recalculateGraphStages();
        Map<Integer, List<IVertex>> verticesByStage = resultGraph.getAllVertices().stream().collect(groupingBy(IVertex::getStage));
        List<Map<Integer, List<IVertex>>> verticesByStageByApplicationIndex = verticesByStage.values().stream().map(x -> x.stream().collect(groupingBy(IVertex::getApplicationIndex))).toList();
        for (var stageVertices: verticesByStageByApplicationIndex) {
            var verticesByApplication = stageVertices.values();
            int index = 0;
            for (var vertexList: verticesByApplication) {
                for (var vertex: vertexList) {
                    vertex.setApplicationIndex(index);
                }
                index++;
            }
        }

        //printGraph(resultGraph);
        return resultGraph;
    }

    private IGraph addVerticesToGraph(IGraph graph, List<IVertex> vertices) {
        for (var vertex : vertices) {
            graph.addVertex(vertex);
        }
        return graph;
    }

    private void recursiveAddEdges(IGraph graph, Feature currentParentFeature, List<IVertex> previousChildVertices) {
        var connectedFeatures = this._FeatureModel.featureConnectivityInformation.featureConnectivityMap.get(currentParentFeature.getName());
        var currentChildVertices = graph.getAllVertices().stream()
                        .filter(x -> getChildFeatures(currentParentFeature)
                        .stream().map(Feature::getName).toList().contains(x.getLabel())).toList();

        for (var previousChildVertex : previousChildVertices) {
            for (var currentChildVertex : currentChildVertices) {
                if (!foundExcludes(previousChildVertex, currentChildVertex)) {
                    var previousChildEdges = graph.getOutgoingEdges(previousChildVertex);
                    if (!previousChildEdges.stream().map(Edge::getDestination).toList().contains(currentChildVertex)) {
                        var edge = new Edge(previousChildVertex, currentChildVertex, _NextEdgeID);
                        _NextEdgeID++;

                        graph.addEdge(edge);
                    }
                }
            }
        }

        if (!connectedFeatures.isEmpty()) {
            for (var connectedFeature : connectedFeatures) {
                recursiveAddEdges(graph, connectedFeature, currentChildVertices);
            }
        }
    }

    private boolean foundExcludes(IVertex firstVertex, IVertex secondVertex) {
        var foundExcludes = _FeatureModel.crossTreeConstraints.stream().filter(constraint -> constraint.getSource().getName().equals(firstVertex.getLabel()) && constraint.getTarget().getName().equals(secondVertex.getLabel()) && constraint.getRelation().equals("excludes")).toList();
        return foundExcludes.size() > 0;
    }

    private Feature findStartFeature(Map<String, List<Feature>> featureConnectivityMap, List<Feature> features) {
        var connectedFeatures = featureConnectivityMap.values();
        var uniqueConnectedFeatureNames = connectedFeatures.stream().flatMap(List::stream).distinct().map(Feature::getName).toList();

        var startFeature = features.stream().filter(x -> !uniqueConnectedFeatureNames.contains(x.getName()) && featureConnectivityMap.containsKey(x.getName())).toList();

        if (startFeature.size() == 1)
            return startFeature.get(0);

        return null;
    }

    private List<Feature> getChildFeatures(Feature feature) {
        return _FeatureModel.features.stream()
                .filter(x -> x.getParentFeatureName() != null && x.getParentFeatureName().equals(feature.getName()))
                .toList();
    }

    //For debugging purposes
    private void printGraph(IGraph graph) {
        System.out.println(" ");
        System.out.println("GRAPH: ");
        for (var vertex: graph.getAllVertices()) {
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(vertex.getLabel());
            System.out.println("successors: ");
            for (var edge : graph.getAllEdges()) {
                if (edge.getDestination() != vertex)System.out.print(edge.getDestination().getLabel() + " ");
            }
        }
    }
}
