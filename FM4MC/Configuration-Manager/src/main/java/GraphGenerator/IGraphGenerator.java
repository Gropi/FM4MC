package GraphGenerator;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.FeatureConnectivityInformation;
import Structures.Graph.Graph;
import Structures.IGraph;

import java.util.List;

/**
 * Common interface for graph generator implementations.
 */
public interface IGraphGenerator {
    Graph generateGraph(List<PartialConfiguration> configuration,
                        FeatureConnectivityInformation featureConnectivityInformation);

    void recalculateIndices(IGraph graph);
}
