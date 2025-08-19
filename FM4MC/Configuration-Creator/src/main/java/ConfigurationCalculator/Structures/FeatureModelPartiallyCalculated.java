package ConfigurationCalculator.Structures;

import FeatureModelReader.Structures.Feature;
import FeatureModelSlicer.Structures.FeatureModelSliced;

import java.util.ArrayList;
import java.util.List;

public class FeatureModelPartiallyCalculated extends FeatureModelSliced {
    public List<List<PartialConfiguration>> configurationsPerPartialFeatureModel = new ArrayList<>();
    public List<List<Feature>> abstractConfigurations = new ArrayList<>();

    public FeatureModelPartiallyCalculated(FeatureModelSliced featureModelSliced) {
        super(featureModelSliced);
    }
}
