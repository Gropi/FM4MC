package FeatureModelSlicer.Structures;

import FeatureModelReader.Structures.CrossTreeConstraint;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;

import java.util.ArrayList;
import java.util.List;

public class FeatureModelSliced extends FeatureModelRead {
    public List<List<Feature>> partialConcreteFeatureModels = new ArrayList<>();
    public List<Feature> abstractLayerFeatureModels = new ArrayList<>();
    public List<List<int[]>> partialFeatureModelClauses = new ArrayList<>();
    public List<List<int[]>> abstractLayerClauses = new ArrayList<>();

    public FeatureModelSliced(FeatureModelRead featureModelRead) {
        super(featureModelRead);
    }

    public FeatureModelSliced(FeatureModelSliced featureModelSliced) {
        super(featureModelSliced);
        this.partialConcreteFeatureModels = new ArrayList<>(featureModelSliced.partialConcreteFeatureModels);
        this.abstractLayerFeatureModels = new ArrayList<>(featureModelSliced.abstractLayerFeatureModels);
        this.partialFeatureModelClauses = new ArrayList<>(featureModelSliced.partialFeatureModelClauses);
        this.abstractLayerClauses = new ArrayList<>(featureModelSliced.abstractLayerClauses);
    }

}
