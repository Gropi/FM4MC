package FeatureModelReader.Structures;

import java.util.List;
import java.util.Map;

/**
 * Holds connectivity information for features such as a call graph and start
 * features used during slicing.
 */
public class FeatureConnectivityInformation {

    public Map<String, List<Feature>> featureConnectivityMap;
    public Feature abstractStartFeature;
    public Feature startFeature;

    /**
     * Creates an empty connectivity info object.
     */
    public FeatureConnectivityInformation() {}

    /**
     * Creates connectivity info with the given map and start features.
     */
    public FeatureConnectivityInformation(Map<String, List<Feature>> featureConnectivityMap, Feature abstractStartFeature, Feature startFeature)
    {
        this.featureConnectivityMap = featureConnectivityMap;
        this.abstractStartFeature = abstractStartFeature;
        this.startFeature = startFeature;
    }
}
