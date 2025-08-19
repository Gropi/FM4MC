package Structures.Graph.interfaces;

import java.util.List;

public interface IVirtualVertex extends IVertex {
    List<IVertex> getAlternatives();

    void addAlternative(IVertex vertex);

    void removeAlternative(IVertex vertex);

    boolean contains(IVertex destination);
}
