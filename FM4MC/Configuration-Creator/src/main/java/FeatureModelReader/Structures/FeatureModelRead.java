package FeatureModelReader.Structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic in-memory representation of a feature model containing features,
 * connectivity information and cross-tree constraints.
 */
public class FeatureModelRead {
    /** all features of the model */
    public List<Feature> features;
    /** connectivity data between features */
    public FeatureConnectivityInformation featureConnectivityInformation;
    /** global cross tree constraints */
    public List<CrossTreeConstraint> crossTreeConstraints;


    /**
     * Creates an empty model.
     */
    public FeatureModelRead() {
        features = new ArrayList<>();
        featureConnectivityInformation = new FeatureConnectivityInformation();
        crossTreeConstraints = new ArrayList<>();
    }

    /**
     * Copy constructor.
     */
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
