package Structures.Graph.SimplifiedGraph;

import Structures.Graph.Graph;
import Structures.Graph.interfaces.IVertex;
import Structures.IGraph;

import java.util.*;
import java.util.stream.Collectors;

public class GraphSimplifier {

    public ArrayList<IGraph> getSubgraphs(IGraph graph) {
        return getSubgraphs(graph, getSubgraphIndicesIterative(graph));
    }

    public ArrayList<IGraph> getSubgraphs(IGraph graph, ArrayList<ArrayList<Integer>> subgraphIndices) {
        var result = new ArrayList<IGraph>();
        var start = graph.getStart();

        for (var subgraph : subgraphIndices) {
            var newSubGraph = new Graph(new Random().nextInt(10000), start, "");

            var vertices = graph.getAllVertices();
            var verticesToAdd = vertices.stream().filter(x -> subgraph.get(x.getStage()) == x.getApplicationIndex()).toList();
            var edges = graph.getAllEdges();
            var edgesToAdd = edges.stream().filter(x -> verticesToAdd.contains(x.getSource()) && verticesToAdd.contains(x.getDestination())).collect(Collectors.toList());

            for (var vertex : verticesToAdd) {
                if (vertex.equals(start)) {
                    continue;
                }
                newSubGraph.addVertex(vertex);
            }

            for (var edge : edgesToAdd) {
                newSubGraph.addEdge(edge);
            }

            result.add(newSubGraph);
        }

        return result;
    }

    public ArrayList<ArrayList<Integer>> getSubgraphIndices(IGraph graph) {
        var temporarySubgraphs = new ArrayList<ArrayList<Integer>>();
        var start = graph.getStart();
        var currentSubgraph = new ArrayList<Integer>();
        currentSubgraph.add(start.getApplicationIndex());

        getSubgraphIndicesForVertex(graph, start, temporarySubgraphs, currentSubgraph);

        return filterDuplicates(temporarySubgraphs);
    }

    private void getSubgraphIndicesForVertex(IGraph graph, IVertex currentVertex, List<ArrayList<Integer>> subgraphs, List<Integer> currentSubgraph) {
        var outgoingEdges = graph.getOutgoingEdges(currentVertex);
        if (subgraphs.contains(currentSubgraph) && !graph.isEndVertex(currentVertex)) {
            subgraphs.remove(currentSubgraph);
        }

        var newSubgraphs = new HashMap<Integer, ArrayList<Integer>>();

        for (var edge : outgoingEdges) {
            var index = edge.getDestination().getApplicationIndex();
            if (!newSubgraphs.containsKey(index)) {
                var newList = new ArrayList<Integer>();
                for (var element : currentSubgraph) {
                    newList.add(element);
                }
                newList.add(index);
                newSubgraphs.put(index, newList);
            }
        }

        for (var index : newSubgraphs.keySet()) {
            subgraphs.add(newSubgraphs.get(index));
        }

        for (var edge : outgoingEdges) {
            getSubgraphIndicesForVertex(graph, edge.getDestination(), subgraphs, newSubgraphs.get(edge.getDestination().getApplicationIndex()));
        }
    }

    public ArrayList<ArrayList<Integer>> getSubgraphIndicesIterative(IGraph graph) {
        var subgraphs = new ArrayList<ArrayList<Integer>>();
        var initialList = new ArrayList<Integer>();
        initialList.add(0);
        subgraphs.add(initialList);

        var indices = new Hashtable<Integer, Hashtable<Integer, ArrayList<Integer>>>();
        var vertices = graph.getAllVertices();

        for(var vertex : vertices) {
            var stage = vertex.getStage();
            var stageDictionary = indices.get(stage);
            if(stageDictionary == null) {
                stageDictionary = new Hashtable<>();
                indices.put(stage, stageDictionary);
            }

            var application = vertex.getApplicationIndex();
            var applicationIndices = stageDictionary.get(application);
            if(applicationIndices == null) {
                applicationIndices = new ArrayList<>();
                stageDictionary.put(application, applicationIndices);
            }

            var edges = graph.getOutgoingEdges(vertex);
            for(var edge : edges) {
                var successorApplication = edge.getDestination().getApplicationIndex();
                if(!applicationIndices.contains(successorApplication)) {
                    applicationIndices.add(successorApplication);
                }
            }
        }

        for(int i = 0; i < indices.keySet().size(); i++) {
            var newSubgraphs = new ArrayList<ArrayList<Integer>>();

            var appIndices = indices.get(i);
            var apps = appIndices.keySet();
            for(var a : apps) {
                var listsSoFar = subgraphs.stream().filter(x -> x.get(x.size()-1).equals(a)).toList();
                var indicesToAdd = appIndices.get(a);

                for(var list : listsSoFar){
                    for(var index : indicesToAdd) {
                       var newList = new ArrayList<Integer>();
                       newList.addAll(list);
                       newList.add(index);
                       newSubgraphs.add(newList);
                    }
                }
            }

            if(newSubgraphs.size() >= subgraphs.size()) {
                subgraphs = newSubgraphs;
            }
        }

        return subgraphs;
    }

    private ArrayList<ArrayList<Integer>> filterDuplicates(List<ArrayList<Integer>> temporarySubgraphs) {
        var finalSubgraphs = new ArrayList<ArrayList<Integer>>();

        for (var candidate : temporarySubgraphs) {
            var valid = true;

            for (var subgraph : finalSubgraphs) {
                var invalid = true;

                for (int i = 0; i < subgraph.size(); i++) {
                    if (subgraph.get(i) != candidate.get(i)) {
                        invalid = false;
                        break;
                    }
                }

                if (invalid) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                finalSubgraphs.add(candidate);
            }
        }

        return finalSubgraphs;
    }

}
