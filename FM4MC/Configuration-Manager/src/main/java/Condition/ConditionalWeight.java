package Condition;

import Structures.Graph.interfaces.IVertex;

/**
 * Represents a cost associated with a specific vertex in the graph.
 */
public class ConditionalWeight {

    private IVertex vertex;
    private int costs;

    /**
     * Creates a conditional weight for the given vertex.
     *
     * @param vertex vertex to which the cost belongs
     * @param costs  numeric cost value
     */
    public ConditionalWeight(IVertex vertex, int costs) {
        this.vertex = vertex;
        this.costs = costs;
    }

    /**
     * @return vertex that this cost is associated with
     */
    public IVertex getVertex() {
        return vertex;
    }

    /**
     * @return stored cost value
     */
    public int getCosts() {
        return costs;
    }
}
