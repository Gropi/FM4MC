package Helper;

import Monitoring.Enums.MeasurableValues;
import Structures.Graph.Edge;
import Structures.Graph.Graph;
import Structures.Graph.Vertex;
import Structures.Graph.interfaces.IVertex;
import Structures.IGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataCreator {
    /**
     * Building a tree like:
     *                          A(Start)
     *            A1              A2            A3
     *       A1.1 A1.2      A2.1  A2.2
     *                          B(End)
     * The Fastest Way: A3 -> End; cost optimum for
     */
    public Graph generateWithOnlyOneCost() {
        var costName = MeasurableValues.TIME.name();
        var start = new Vertex("start", 31 * "start".hashCode(), "");
        start.updateWeight(costName, 1);
        var graph = new Graph(new Random().nextInt(10000), start, true, "");

        var end = createVertex("end", 31 * "end".hashCode() + costName.hashCode(), costName, 1);
        graph.addVertex(end);

        for (int i = 0; i < 3; i++) {
            if (i == 2) {
                var vertex = createVertex("A3", 31 * "A3".hashCode() + costName.hashCode(), costName, 1);
                graph.addVertex(vertex);
                addEdge(graph, start, vertex, costName, 1);
                addEdge(graph, vertex, end, costName, 1);
            } else {
                var rootLabel = "A" + (i + 1);
                var currentNode = createVertex(rootLabel, 31 * rootLabel.hashCode() + costName.hashCode(), costName, i + 2);
                graph.addVertex(currentNode);
                addEdge(graph, start, currentNode, costName, 1);
                for (int z = 0; z < 2; z++) {
                    var label = rootLabel + "." + (z + 1);
                    var branchVertex = createVertex(label, 31 * label.hashCode() + costName.hashCode(), costName, z + 2);
                    graph.addVertex(branchVertex);
                    addEdge(graph, currentNode, branchVertex, costName, 1);
                    addEdge(graph, branchVertex, end, costName, 1);
                }
            }
        }

        return graph;
    }

    /**
     * Building a tree like:
     *                  A0(Start)
     *            A1              A2
     *            A3              A4
     *                  A5(End)
     */
    public Graph generateGraphToExploitOldDijkstra() {
        var costName = MeasurableValues.TIME.name();

        var vertices = new ArrayList<IVertex>();
        for(int i = 0; i < 6 ; i++){
            var vertex = new Vertex("Vertex"+i, i, "");
            vertex.updateWeight(costName, 0);
            vertices.add(vertex);
        }

        var edge1 = new Edge(vertices.get(0),vertices.get(1), 101);
        var edge2 = new Edge(vertices.get(0),vertices.get(2), 102);
        var edge3 = new Edge(vertices.get(2),vertices.get(1), 103);
        var edge4 = new Edge(vertices.get(1),vertices.get(3), 104);
        var edge5 = new Edge(vertices.get(2),vertices.get(4), 105);
        var edge6 = new Edge(vertices.get(3),vertices.get(5), 106);
        var edge7 = new Edge(vertices.get(4),vertices.get(5), 107);

        edge1.updateWeight(costName, 5);
        edge2.updateWeight(costName, 1);
        edge3.updateWeight(costName, 1);
        edge4.updateWeight(costName, 1);
        edge5.updateWeight(costName, 3);
        edge6.updateWeight(costName, 1);
        edge7.updateWeight(costName, 1);

        var graph = new Graph(new Random().nextInt(10000), vertices.get(0), true, "");

        for(int i = 1; i < vertices.size(); i++) {
            graph.addVertex(vertices.get(i));
        }

        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);
        graph.addEdge(edge5);
        graph.addEdge(edge6);
        graph.addEdge(edge7);

        return graph;
    }

    public Graph generateMultiParetoGraph() {
        var costName = MeasurableValues.TIME.name();
        var costName2 = MeasurableValues.RAM.name();

        var vertices = new ArrayList<IVertex>();
        for(int i = 0; i < 6 ; i++){
            var vertex = new Vertex("Vertex"+i, i, "");
            vertex.updateWeight(costName, 0);
            vertices.add(vertex);
        }

        var edge1 = new Edge(vertices.get(0),vertices.get(1), 101);
        var edge2 = new Edge(vertices.get(0),vertices.get(2), 102);
        var edge3 = new Edge(vertices.get(2),vertices.get(1), 103);
        var edge4 = new Edge(vertices.get(1),vertices.get(3), 104);
        var edge5 = new Edge(vertices.get(2),vertices.get(4), 105);
        var edge6 = new Edge(vertices.get(3),vertices.get(5), 106);
        var edge7 = new Edge(vertices.get(4),vertices.get(5), 107);

        edge1.updateWeight(costName, 5);
        edge2.updateWeight(costName, 1);
        edge3.updateWeight(costName, 1);
        edge4.updateWeight(costName, 1);
        edge5.updateWeight(costName, 3);
        edge6.updateWeight(costName, 1);
        edge7.updateWeight(costName, 1);

        edge1.updateWeight(costName2, 1);
        edge2.updateWeight(costName2, 2);
        edge3.updateWeight(costName2, 2);
        edge4.updateWeight(costName2, 1);
        edge5.updateWeight(costName2, 2);
        edge6.updateWeight(costName2, 1);
        edge7.updateWeight(costName2, 1);

        var graph = new Graph(new Random().nextInt(10000), vertices.get(0), true, "");

        for(int i = 1; i < vertices.size(); i++) {
            graph.addVertex(vertices.get(i));
        }

        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);
        graph.addEdge(edge5);
        graph.addEdge(edge6);
        graph.addEdge(edge7);

        return graph;
    }

    /**
     * Building a tree like:
     *                          A(Start)
     *            A1              A2            A3
     *       A1.1 A1.2      A2.1  A2.2
     *                          B(End)
     * The Fastest Way for Greedy: A2.2 -> End; cost optimum for
     * The Fastest Way for Dijkstra: A3 -> End; cost optimum for
     */
    public Graph generateGraph() {
        var start = new Vertex("start", 31 * "start".hashCode(), "");
        var costNames = generateWeightNames();
        var intCosts = generateIntWeights();
        var longCosts = generateIntWeights();
        var graph = new Graph(new Random().nextInt(10000), start, true, "");

        var end = createVertex("end", 31 * "end".hashCode() + costNames.hashCode(), costNames, intCosts, 1);
        graph.addVertex(end);

        for (int i = 0; i < 3; i++) {
            if (i == 2) {
                var vertex = createVertex("A3", 31 * "A3".hashCode() + costNames.hashCode(), costNames, intCosts, 10);
                graph.addVertex(vertex);
                addEdge(graph, start, vertex, costNames, longCosts, 20);
                addEdge(graph, vertex, end, costNames, longCosts, 1);
            } else {
                var rootLabel = "A" + (i + 1);
                var multiplier = i == 1 ? 1 : i + 4;
                var currentNode = createVertex(rootLabel,31 * rootLabel.hashCode() + costNames.hashCode(), costNames, intCosts, multiplier);
                graph.addVertex(currentNode);
                addEdge(graph, start, currentNode, costNames, longCosts, multiplier);
                for (int z = 0; z < 2; z++) {
                    var label = rootLabel + "." + (z + 1);
                    var branchVertex = createVertex(label, 31 * label.hashCode() + costNames.hashCode(), costNames, intCosts, 10 - z);
                    graph.addVertex(branchVertex);
                    addEdge(graph, currentNode, branchVertex, costNames, longCosts, 10 - z);
                    addEdge(graph, branchVertex, end, costNames, longCosts, 19);
                }
            }
        }

        // TODO: implement Markov decision process

        return graph;
    }

    private Vertex createVertex(String label, int id, String costName, Integer weight) {
        var vertex = new Vertex(label, id, "");
        vertex.updateWeight(costName, weight);
        return vertex;
    }

    private Vertex createVertex(String label, int id, List<String> costNames, List<Integer> weights, Integer multiplier) {
        var vertex = new Vertex(label, id, "");

        for (int i = 1; i < costNames.size(); i++) {
            vertex.updateWeight(costNames.get(i), weights.get(i) * multiplier);
        }

        return vertex;
    }

    private Vertex addEdge(IGraph graph, Vertex start, Vertex end, String costName, Integer weight) {
        graph.addEdge(start.getId(), end.getId(), 31 * start.hashCode() + end.hashCode());
        var edges = graph.getEdgesBetweenVertices(start, end);
        edges.get(0).updateWeight(costName, weight);

        return start;
    }

    private Vertex addEdge(IGraph graph, Vertex start, Vertex end, List<String> costNames, List<Integer> weights, Integer multiplier) {
        graph.addEdge(start.getId(), end.getId(), 31 * start.hashCode() + end.hashCode());

        var edges = graph.getEdgesBetweenVertices(start, end);
        for (int i = 0; i < 1; i++) {
            edges.get(0).updateWeight(costNames.get(i), weights.get(i) * multiplier);
        }

        return start;
    }

    private List<String> generateWeightNames() {
        var list = new ArrayList<String>();

        list.add(MeasurableValues.LATENCY.name());
        list.add(MeasurableValues.TIME.name());
        list.add(MeasurableValues.CPU.name());
        list.add(MeasurableValues.RAM.name());

        return list;
    }

    private List<Integer> generateIntWeights() {
        var list = new ArrayList<Integer>();

        for (int i = 0; i < 4; i++) {
            list.add(1);
        }

        return list;
    }

    private List<Long> generateLongWeights() {
        var list = new ArrayList<Long>();

        for (int i = 0; i < 4; i++) {
            list.add(1L);
        }

        return list;
    }
}
