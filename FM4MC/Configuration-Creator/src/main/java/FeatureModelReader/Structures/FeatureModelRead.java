package FeatureModelReader.Structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureModelRead {
    public List<Feature> features;
    public FeatureConnectivityInformation featureConnectivityInformation;
    public List<CrossTreeConstraint> crossTreeConstraints;


    public FeatureModelRead() {
        features = new ArrayList<>();
        featureConnectivityInformation = new FeatureConnectivityInformation();
        crossTreeConstraints = new ArrayList<>();
    }

    public FeatureModelRead(FeatureModelRead fm) {
        features = new ArrayList<>(fm.features);
        featureConnectivityInformation = new FeatureConnectivityInformation(
                new HashMap<>(fm.featureConnectivityInformation.featureConnectivityMap),
                fm.featureConnectivityInformation.abstractStartFeature,
                fm.featureConnectivityInformation.startFeature
        );

        crossTreeConstraints = new ArrayList<>(fm.crossTreeConstraints);
    }
}
