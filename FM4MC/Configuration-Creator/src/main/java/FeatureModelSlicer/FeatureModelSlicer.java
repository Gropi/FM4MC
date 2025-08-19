package FeatureModelSlicer;

import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelReader.Structures.FeatureModelRelation;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureModelSlicer {
    private FeatureModelSliced m_FeatureModelSliced;

    private final Logger _Logger;

    public FeatureModelSlicer(Logger logger) {
        _Logger = logger;
    }

    public FeatureModelSliced sliceFeatureModel(FeatureModelRead readFeatureModel, int threshold) {
        // List of slices; each slice is a list of features representing a sequential chain.
        m_FeatureModelSliced = new FeatureModelSliced(readFeatureModel);

        // Find the start feature using your connectivity info.
        Feature startFeature = readFeatureModel.featureConnectivityInformation.abstractStartFeature;
        m_FeatureModelSliced.abstractLayerFeatureModels = buildAbstractLayerNonRecursive(readFeatureModel);

        // Use recursive slicing based on the call graph.
        m_FeatureModelSliced.partialConcreteFeatureModels = sliceFeatureModelByCallGraph(startFeature, threshold);

        // Optionally, you can log or process m_SlicedFeatureModels.
        return m_FeatureModelSliced;
    }

    private List<Feature> buildAbstractLayerNonRecursive(FeatureModelRead readFeatureModel) {
        return readFeatureModel.features.stream().filter(f -> !f.getChildren().isEmpty()).collect(Collectors.toList());
    }

    /**
     * Recursively slice the feature model based on call graph rules.
     * Each slice is a list of features forming a sequential chain.
     */
    private List<List<Feature>> sliceFeatureModelByCallGraph(Feature startFeature, int threshold) {
        var result = new ArrayList<List<Feature>>();
        // Start recursive slicing with an empty current slice and estimated configs = 1.
        sliceRecursively(startFeature, threshold, new ArrayList<>(), 1, result, new HashSet<Feature>());
        return result;
    }

    /**
     * Helper method to recursively slice the feature chain.
     *
     * @param current         The current abstract feature.
     * @param threshold       The threshold for estimated configuration complexity.
     * @param currentSlice    The current slice being built.
     * @param currentEstimate The estimated configuration count so far.
     * @param result          The global list of slices.
     */
    private void sliceRecursively(Feature current, int threshold, List<Feature> currentSlice, int currentEstimate, List<List<Feature>> result, Set<Feature> visited) {
        if (visited.contains(current)) {
            return;
        }
        // Add the current feature to the current slice.
        currentSlice.add(current);
        visited.add(current);
        currentEstimate = estimateConfigs(currentEstimate, current);

        // Get the successors from connectivity (reachableAbstractFeatures) of the current feature.
        var successors = m_FeatureModelSliced.featureConnectivityInformation.featureConnectivityMap.get(current.getName());
        if (successors == null || successors.isEmpty()) {
            // No successors: end this slice.
            result.add(new ArrayList<>(currentSlice));
            return;
        }
        // If more than one successor: rule 1 (parallel execution) - end current slice and start new slices for each successor.
        if (successors.size() > 1) {
            result.add(new ArrayList<>(currentSlice));
            for (var successor : successors) {
                // Start a new slice for each successor.
                sliceRecursively(successor, threshold, new ArrayList<>(), 1, result, visited);
            }
            return;
        }
        // Only one successor.
        var next = successors.get(0);
        // Check rule 2: if next has more than one predecessor.
        if (moreThanOnePredecessorForSuccessor(next)) {
            result.add(new ArrayList<>(currentSlice));
            // Start new slice with next.
            sliceRecursively(next, threshold, new ArrayList<>(), 1, result, visited);
            return;
        }
        // Check rule 3: if estimated configs with next exceeds threshold.
        if (estimateConfigs(currentEstimate, next) > threshold) {
            result.add(new ArrayList<>(currentSlice));
            sliceRecursively(next, threshold, new ArrayList<>(), 1, result, visited);
            return;
        }
        // Continue the same slice with next feature.
        sliceRecursively(next, threshold, currentSlice, currentEstimate, result, visited);
    }

    /**
     * Estimate the number of valid configurations when adding the current abstract feature.
     * This is based on the relation of the child features.
     */
    private int estimateConfigs(int currentEstimatedConfigs, Feature abstractFeature) {
        var childFeatures = abstractFeature.getChildren();
        // If there are no direct child features, return current estimate.
        if (childFeatures.isEmpty()) {
            return currentEstimatedConfigs;
        }
        // We assume all child features of the abstract feature share the same relation.
        var relation = childFeatures.get(0).getRelation();
        if (relation.equals(FeatureModelRelation.ALTERNATIVE)) {
            return currentEstimatedConfigs * childFeatures.size();
        } else {
            return currentEstimatedConfigs;
        }
    }

    /**
     * Check if the successor feature has more than one predecessor.
     */
    private boolean moreThanOnePredecessorForSuccessor(Feature successor) {
        var keySet = m_FeatureModelSliced.featureConnectivityInformation.featureConnectivityMap.keySet();
        int predecessors = 0;
        for (var key : keySet) {
            if (m_FeatureModelSliced.featureConnectivityInformation.featureConnectivityMap.get(key).contains(successor)) {
                predecessors++;
            }
            if (predecessors > 1) {
                return true;
            }
        }
        return false;
    }
}
