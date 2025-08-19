package ConfigurationCalculator.Structures;

import FeatureModelReader.Structures.Feature;
import FeatureModelSlicer.Structures.FeatureModelSliced;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sliced feature model for which configurations have already been
 * calculated. Stores both concrete configurations per partial feature model and
 * the resulting abstract configurations.
 */
public class FeatureModelPartiallyCalculated extends FeatureModelSliced {
    /** configurations for each partial feature model */
    public List<List<PartialConfiguration>> configurationsPerPartialFeatureModel = new ArrayList<>();
    /** configurations on the abstract layer */
    public List<List<Feature>> abstractConfigurations = new ArrayList<>();

    /**
     * Creates a new instance based on a sliced model.
     */
    public FeatureModelPartiallyCalculated(FeatureModelSliced featureModelSliced) {
        super(featureModelSliced);
    }
}
