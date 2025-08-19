package FeatureModelReader.Structures;

import java.util.List;
import java.util.Map;

public class FeatureConnectivityInformation {

    public Map<String, List<Feature>> featureConnectivityMap;
    public Feature abstractStartFeature;
    public Feature startFeature;

    public FeatureConnectivityInformation() {}

    public FeatureConnectivityInformation(Map<String, List<Feature>> featureConnectivityMap, Feature abstractStartFeature, Feature startFeature)
    {
        this.featureConnectivityMap = featureConnectivityMap;
        this.abstractStartFeature = abstractStartFeature;
        this.startFeature = startFeature;
    }
}
