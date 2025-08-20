package FeatureModelSlicer.Structures;

import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of {@link FeatureModelRead} that also holds slicing information
 * such as partial feature models and their CNF clauses.
 */
public class FeatureModelSliced extends FeatureModelRead {
    /** partial concrete feature models derived from the abstract layer */
    public List<List<Feature>> partialConcreteFeatureModels = new ArrayList<>();
    /** all abstract features remaining after slicing */
    public List<Feature> abstractLayerFeatureModels = new ArrayList<>();
    /** CNF clauses for each partial feature model */
    public List<List<int[]>> partialFeatureModelClauses = new ArrayList<>();
    /** CNF clauses for the abstract layer */
    public List<List<int[]>> abstractLayerClauses = new ArrayList<>();

    /**
     * Creates a new sliced model based on the read model.
     */
    public FeatureModelSliced(FeatureModelRead featureModelRead) {
        super(featureModelRead);
    }

    /**
     * Copy constructor.
     */
    public FeatureModelSliced(FeatureModelSliced featureModelSliced) {
        super(featureModelSliced);
        this.partialConcreteFeatureModels = new ArrayList<>(featureModelSliced.partialConcreteFeatureModels);
        this.abstractLayerFeatureModels = new ArrayList<>(featureModelSliced.abstractLayerFeatureModels);
        this.partialFeatureModelClauses = new ArrayList<>(featureModelSliced.partialFeatureModelClauses);
        this.abstractLayerClauses = new ArrayList<>(featureModelSliced.abstractLayerClauses);
    }

}
