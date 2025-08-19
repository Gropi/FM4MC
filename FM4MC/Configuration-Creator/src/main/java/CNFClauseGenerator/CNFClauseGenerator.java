package CNFClauseGenerator;

import FeatureModelReader.Structures.*;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds conjunctive normal form (CNF) clauses for feature models and their
 * sliced variants. Clauses are represented as arrays of integers following the
 * DIMACS convention where positive/negative literals indicate selected or
 * deselected features.
 */
public class CNFClauseGenerator {

    private final Logger _Logger;
    //private FeatureModelSliced _FMSliced;

    /**
     * Creates a new generator.
     *
     * @param logger logger for debug or error output
     */
    public CNFClauseGenerator(Logger logger) {
        _Logger = logger;
    }

    /**
     * Adds the root clause to the list of CNF clauses.
     */
    private void createCNFForRoot(List<int[]> featureModelClausesCNF) {
        featureModelClausesCNF.add(new int[]{1});
    }

    /**
     * Adds all CTCs that have source and target in the given list of features
     * to the CNF clauses and removes them from the list of global CTCs
     *
     * @param features all features of a PFM or full model
     * @param featureModelClausesCNF existing clauses
     * @param crossTreeConstraints list of CTCs
     */
    private void addLocalCTC(List<Feature> features, List<int[]> featureModelClausesCNF, List<CrossTreeConstraint> crossTreeConstraints) {
        var itr = crossTreeConstraints.iterator();
        while (itr.hasNext()) {
            var constraint = itr.next();
            if (features.contains(constraint.getSource()) && features.contains(constraint.getTarget())) {
                if (constraint.getRelation() == CrossTreeConstraintRelation.EXCLUDES) {
                    featureModelClausesCNF.add(new int[]{-constraint.getSource().getIndex(), -constraint.getTarget().getIndex()});
                } else if (constraint.getRelation() == CrossTreeConstraintRelation.REQUIRES) {
                    featureModelClausesCNF.add(new int[]{-constraint.getSource().getIndex(), constraint.getTarget().getIndex()});
                }
                itr.remove();
            }
        }
    }

    /**
     * Creates an empty clause list with a placeholder header.
     *
     * @return initialized list containing only the header clause
     */
    private List<int[]> initializeCNFClauses() {
        var featureModelClausesCNF = new ArrayList<int[]>();
        featureModelClausesCNF.add(new int[]{0, 0});    //empty header
        return featureModelClausesCNF;
    }

    /**
     * Creates CNF clauses for all child parent relations
     *
     * @param childFeatures all children of the parent feature
     * @param parentIndex index of parent feature
     * @return list of CNF clauses
     */
    private List<int[]> createChildFeatureClauses(List<Feature> childFeatures, int parentIndex) {
        var childFeatureClauses = new ArrayList<int[]>();
        if (childFeatures.size() > 1 && childFeatures.get(0).getRelation() == FeatureModelRelation.ALTERNATIVE) {
            childFeatureClauses.addAll(createAlternativeClauses(childFeatures, parentIndex));
        } else if (childFeatures.size() > 1 && childFeatures.get(0).getRelation() == FeatureModelRelation.OR) {
            childFeatureClauses.addAll(createOrClauses(childFeatures, parentIndex));
        } else {
            for (var feature : childFeatures) {
                switch (feature.getRelation()) {
                    case MANDATORY -> childFeatureClauses.addAll(createMandatoryClauses(feature, parentIndex));
                    case OPTIONAL -> childFeatureClauses.addAll(createOptionalClauses(feature, parentIndex));
                }
            }
        }
        return childFeatureClauses;
    }

    /**
     * Generates CNF clauses for an alternative group of child features.
     */
    private List<int[]> createAlternativeClauses(List<Feature> childFeatures, int parentIndex) {
        var alternativeClauses = new ArrayList<int[]>();
        var featureIndices = childFeatures.stream().map(Feature::getIndex).toList();

        // Example (A v B v C v -ROOT)
        var noChildNegativeClause = new ArrayList<>(featureIndices);
        noChildNegativeClause.add(-parentIndex);
        alternativeClauses.add(noChildNegativeClause.stream().mapToInt(index -> index).toArray());

        for (var i = 0; i < featureIndices.size(); i++) {
            var feature = featureIndices.get(i);

            // Example (A v -B v C v ROOT)
            var oneChildNegativeClause = new ArrayList<>(featureIndices);
            oneChildNegativeClause.add(parentIndex);
            oneChildNegativeClause.set(oneChildNegativeClause.indexOf(feature), -feature);
            alternativeClauses.add(oneChildNegativeClause.stream().mapToInt(index -> index).toArray());

            // Example (-A v -B), (-A v -C), (-B v -C)
            for (var j = i + 1; j < featureIndices.size(); j++) {
                alternativeClauses.add(new int[]{-feature, -featureIndices.get(j)});
            }
        }

        return alternativeClauses;
    }

    /**
     * Generates CNF clauses for an OR group of child features.
     */
    private List<int[]> createOrClauses(List<Feature> childFeatures, int parentIndex) {
        var orClauses = new ArrayList<int[]>();
        var featureIndices = childFeatures.stream().map(Feature::getIndex).toList();

        // Example (A v B v C v -ROOT)
        var noChildNegativeClause = new ArrayList<>(featureIndices);
        noChildNegativeClause.add(-parentIndex);
        orClauses.add(noChildNegativeClause.stream().mapToInt(index -> index).toArray());

        for (Integer featureIndex : featureIndices) {
            orClauses.add(new int[]{-featureIndex, parentIndex});
        }

        return orClauses;
    }

    /**
     * Generates CNF clauses for a mandatory relation between parent and child.
     */
    private List<int[]> createMandatoryClauses(Feature childFeature, int parentIndex) {
        var mandatoryClauses = new ArrayList<int[]>();
        var featureIndex = childFeature.getIndex();

        mandatoryClauses.add(new int[]{featureIndex, -parentIndex});
        mandatoryClauses.add(new int[]{-featureIndex, parentIndex});
        return mandatoryClauses;
    }

    /**
     * Generates CNF clauses for an optional relation between parent and child.
     */
    private List<int[]> createOptionalClauses(Feature childFeature, int parentIndex) {
        var optionalClauses = new ArrayList<int[]>();
        var featureIndex = childFeature.getIndex();

        optionalClauses.add(new int[]{-featureIndex, parentIndex});
        return optionalClauses;
    }

    /**
     * updates the header of the CNF clauses
     * the first element is the highest existing literal
     * the second element is the number of clauses
     */
    private void updateHeader(List<int[]> featureModelClausesCNF) {
        var max = 0;
        for (var clause : featureModelClausesCNF) {
            for (int j : clause) {
                if (Math.abs(j) > max)
                    max = Math.abs(j);
            }
        }
        var header = new int[]{max, featureModelClausesCNF.size() - 1};
        featureModelClausesCNF.set(0, header);
    }

    /**
     * creates CNF clauses for all relations between abstract features of the sliced FM
     * also adds clauses for all cross tree constraints that have source and target inside the sliced FM
     *
     * @param fm sliced FM
     * @return CNF clauses
     */
    public List<int[]> createAbstractLayerClauses(FeatureModelSliced fm) {
        List<Feature> abstractLayer = fm.abstractLayerFeatureModels;
        var abstractLayerCNF = initializeCNFClauses();
        createCNFForRoot(abstractLayerCNF);
        abstractLayer.stream().filter(f -> f.getChildren().stream().noneMatch(child -> child.getChildren().isEmpty())).forEach(f -> {
            abstractLayerCNF.addAll(createChildFeatureClauses(f.getChildren(), f.getIndex()));
        });

        addLocalCTC(abstractLayer, abstractLayerCNF, fm.crossTreeConstraints);
        updateHeader(abstractLayerCNF);
        return abstractLayerCNF;
    }

    /**
     * creates CNF clauses for all relations between features of a partial feature model with concrete features
     * also adds clauses for all cross tree constraints between features of the PFM
     *
     * @param abstractParentFeatures list of abstract parent features
     * @param crossTreeConstraints list of CTCs
     * @return CNF clauses
     */
    public List<int[]> createConcretePFMClauses(List<Feature> abstractParentFeatures, List<CrossTreeConstraint> crossTreeConstraints) {
        var concreteConfigurationClauses = initializeCNFClauses();
        List<Feature> featuresInConfiguration = new ArrayList<>();
        for (Feature abstractParentFeature : abstractParentFeatures) {
            concreteConfigurationClauses.add(new int[]{abstractParentFeature.getIndex()});
            concreteConfigurationClauses.addAll(createChildFeatureClauses(abstractParentFeature.getChildren(), abstractParentFeature.getIndex()));
            featuresInConfiguration.add(abstractParentFeature);
            featuresInConfiguration.addAll(abstractParentFeature.getChildren());
        }
        addLocalCTC(featuresInConfiguration, concreteConfigurationClauses, crossTreeConstraints);
        updateHeader(concreteConfigurationClauses);
        return concreteConfigurationClauses;
    }

    /**
     * creates CNF clauses for all relations in the feature model
     *
     * @param featureModelRead non-sliced FM
     * @return CNF clauses
     */
    public List<int[]> createClausesForNonSlicedFM(FeatureModelRead featureModelRead) {
        var clauses = initializeCNFClauses();
        createCNFForRoot(clauses);
        featureModelRead.features.forEach(feature -> {
            clauses.addAll(createChildFeatureClauses(feature.getChildren(), feature.getIndex()));
        });
        addLocalCTC(featureModelRead.features, clauses, featureModelRead.crossTreeConstraints);
        updateHeader(clauses);
        return clauses;
    }
}
