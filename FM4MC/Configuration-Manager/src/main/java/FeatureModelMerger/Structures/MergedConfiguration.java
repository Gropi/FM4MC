package FeatureModelMerger.Structures;


import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.Feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergedConfiguration {

    public List<PartialConfiguration> _PartialConfigurations = new ArrayList<>();

    public String toString() {
        return Arrays.toString(_PartialConfigurations.stream().flatMap(x -> x.getFeatures().stream()).map(Feature::getName).toArray());
    }

    public List<Feature> getUniqueFeatures() {
        return _PartialConfigurations.stream().flatMap(x -> x.getFeatures().stream()).distinct().toList();
    }
}
