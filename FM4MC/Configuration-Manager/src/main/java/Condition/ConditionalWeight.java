package Condition;

import Structures.Graph.interfaces.IVertex;

public class ConditionalWeight {

    private IVertex vertex;
    private int costs;

    public ConditionalWeight(IVertex vertex, int costs) {
        this.vertex = vertex;
        this.costs = costs;
    }

    public IVertex getVertex() {
        return vertex;
    }

    public int getCosts() {
        return costs;
    }
}
