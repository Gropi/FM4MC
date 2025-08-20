package FeatureModelMerger;

import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelMerger.Structures.MergedConfiguration;
import FeatureModelReader.Structures.CrossTreeConstraintRelation;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureConnectivityInformation;
import Filter.FeatureFilter;
import GraphGenerator.GraphGeneratorV2;
import IO.impl.LshwClass;
import Structures.Graph.Graph;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class HardwareSensitiveFeatureModelMerger {

    private final Logger _Logger;
    private FeatureModelPartiallyCalculated partiallyCalculatedFeatureModel;
    private GraphGeneratorV2 graphGenerator = new GraphGeneratorV2();
    private Graph combinedGraph;
    public int validConfigurations = 0;

    public HardwareSensitiveFeatureModelMerger(Logger logger) {
        _Logger = logger;
    }

    public Graph startForTesting(FeatureModelPartiallyCalculated fm, AvailableEdgeHardware edgeHardwareInformation, int maxRequirements) {
        var combinedGraph = start(fm, edgeHardwareInformation, maxRequirements);
        if (combinedGraph != null) {
            combinedGraph.recalculateGraphStages();
            graphGenerator.recalculateIndices(combinedGraph);
        }
        return combinedGraph;
    }

    public Graph start(FeatureModelPartiallyCalculated fm, AvailableEdgeHardware edgeHardwareInformation, int maxRequirements) {
        partiallyCalculatedFeatureModel = fm;
        graphGenerator = new GraphGeneratorV2();
        validConfigurations = 0;

        var featureFilter = new FeatureFilter(_Logger);
        var nonAvailableFeatures = featureFilter.filterFeaturesAgainstEdge(fm.features, edgeHardwareInformation, maxRequirements);

        var configurationsPerFeatureModel = new ArrayList<>(partiallyCalculatedFeatureModel.configurationsPerPartialFeatureModel);
        var mergedConfiguration = new MergedConfiguration();
        var changesToLastConfiguration = new MergedConfiguration();

        mergeConfigurationsRecursive(mergedConfiguration, configurationsPerFeatureModel, changesToLastConfiguration, 0,
                nonAvailableFeatures);
        addConditionalWeights();
        return combinedGraph;
    }

    private void removeParentsFromConnectivityMap(FeatureModelPartiallyCalculated fm, Set<Feature> parents) {
        var featureConnectivityMap = fm.featureConnectivityInformation.featureConnectivityMap;
        var precedingFeaturesToBeRemoved = new HashSet<Feature>();
        for (var parentFeature : parents) {
            featureConnectivityMap.remove(parentFeature.getName());
            featureConnectivityMap.values().forEach(connectedFeatures -> connectedFeatures.remove(parentFeature));
            fm.features.forEach(feature -> {
                if (feature.getSuccessiveFeatures().remove(parentFeature) && feature.getSuccessiveFeatures().isEmpty()) {
                    precedingFeaturesToBeRemoved.add(feature);
                }
            });
        }
        if (!precedingFeaturesToBeRemoved.isEmpty()) {
            removeParentsFromConnectivityMap(fm, precedingFeaturesToBeRemoved);
        }
    }

    private boolean areEqual(Map<LshwClass, Integer> first, Map<LshwClass, Integer> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> e.getValue() <= second.get(e.getKey()));
    }

    private void mergeConfigurationsRecursive(MergedConfiguration currentConfiguration,
                                              List<List<PartialConfiguration>> slicedConfigurations,
                                              MergedConfiguration changesToLastConfiguration,
                                              int index, List<Feature> nonAvailableFeatures) {
        var slicedConfiguration = slicedConfigurations.get(index);
        synchronized (slicedConfiguration) {
            var abstractParents = new HashSet<Feature>();
            // find all not available partial configurations, to delete them from the list of pfms.
            var iterator = slicedConfiguration.iterator();
            while (iterator.hasNext()) {
                var config = iterator.next();
                if (config.getFeatures().stream().anyMatch(nonAvailableFeatures::contains)) {
                    abstractParents.addAll(config.getAbstractParent());
                    iterator.remove();
                }
            }

            if (slicedConfiguration.isEmpty()) {
                removeParentsFromConnectivityMap(partiallyCalculatedFeatureModel, abstractParents);
                slicedConfigurations.remove(slicedConfiguration);
                if (slicedConfigurations.size() > index)
                    mergeConfigurationsRecursive(currentConfiguration, slicedConfigurations, changesToLastConfiguration,
                            index, nonAvailableFeatures);
                else
                    return;
            }

            for (var partialConfiguration : slicedConfiguration) {
                // Check if the configuration fits with the current abstract configuration.
                // For concrete configurations, ensure that hardware requirements are met.
                if (noContradictingCrossTreeConstraints(currentConfiguration._PartialConfigurations, partialConfiguration.getFeatures())) {
                    // (Optional) For abstract configurations: add additional checks if needed.
                    // e.g., if abstract configuration does not match the current abstract layer, skip it.

                    var directPredecessorConfigurations = getPredecessorConfigurations(currentConfiguration._PartialConfigurations,
                            partialConfiguration,
                            partiallyCalculatedFeatureModel.featureConnectivityInformation);
                    var directPredecessorConfigurationsNotContained = directPredecessorConfigurations.stream()
                            .filter(x -> !changesToLastConfiguration._PartialConfigurations.contains(x))
                            .toList();
                    changesToLastConfiguration._PartialConfigurations.addAll(directPredecessorConfigurationsNotContained);
                    changesToLastConfiguration._PartialConfigurations.add(partialConfiguration);

                    currentConfiguration._PartialConfigurations.add(partialConfiguration);

                    if (index + 1 < slicedConfigurations.size()) {
                        mergeConfigurationsRecursive(currentConfiguration, slicedConfigurations, changesToLastConfiguration,
                                index + 1, nonAvailableFeatures);
                        changesToLastConfiguration._PartialConfigurations.remove(partialConfiguration);
                        currentConfiguration._PartialConfigurations.remove(partialConfiguration);
                    } else {
                        validConfigurations++;
                        var graphForConfiguration = graphGenerator.generateGraph(changesToLastConfiguration._PartialConfigurations,
                                partiallyCalculatedFeatureModel.featureConnectivityInformation);
                        if (combinedGraph == null) {
                            combinedGraph = graphForConfiguration;
                        } else {
                            combinedGraph.uniteWithGraph(graphForConfiguration);
                        }
                        changesToLastConfiguration._PartialConfigurations = new ArrayList<>();
                        currentConfiguration._PartialConfigurations.remove(partialConfiguration);
                    }
                }
            }
        }
    }

    private List<PartialConfiguration> getPredecessorConfigurations(
            List<PartialConfiguration> currentConfiguration,
            PartialConfiguration configurationToMerge,
            FeatureConnectivityInformation featureConnectivityInformation) {

        if (currentConfiguration.isEmpty())
            return new ArrayList<>();

        if (currentConfiguration.size() == 1)
            return new ArrayList<>(currentConfiguration);

        // Preliminary step: create a set with the names of the abstract parents of configurationToMerge.
        Set<String> mergeAbstractParentNames = configurationToMerge.getAbstractParent()
                .stream()
                .map(Feature::getName)
                .collect(Collectors.toSet());

        // Compute the set of "abstract predecessor features":
        // - A connectedFeatureName is a candidate if it is NOT contained in mergeAbstractParentNames
        // - AND if its associated list contains at least one feature that appears in configurationToMerge.getAbstractParent().
        Set<String> abstractPredecessorNames = featureConnectivityInformation.featureConnectivityMap.keySet()
                .stream()
                .filter(connectedFeatureName ->
                        !mergeAbstractParentNames.contains(connectedFeatureName) &&
                                featureConnectivityInformation.featureConnectivityMap.get(connectedFeatureName)
                                        .stream()
                                        .anyMatch(feature -> configurationToMerge.getAbstractParent().contains(feature))
                )
                .collect(Collectors.toSet());

        // Now filter currentConfiguration:
        // A PartialConfiguration is returned if at least one of its abstract parents (by name) is contained in abstractPredecessorNames.
        return currentConfiguration.stream()
                .filter(config -> config.getAbstractParent()
                        .stream()
                        .map(Feature::getName)
                        .anyMatch(abstractPredecessorNames::contains))
                .collect(Collectors.toList());
    }

    private PartialConfiguration cloneConfiguration(PartialConfiguration toClone) {
        var clonedConfiguration = new PartialConfiguration();
        clonedConfiguration.setFeatures(toClone.getFeatures());
        return clonedConfiguration;
    }

    private FeatureModelPartiallyCalculated clonePartialFM() {
        var clone = new FeatureModelPartiallyCalculated(partiallyCalculatedFeatureModel);
        clone.configurationsPerPartialFeatureModel = partiallyCalculatedFeatureModel.configurationsPerPartialFeatureModel;
        return clone;
    }

    private boolean noContradictingCrossTreeConstraints(List<PartialConfiguration> alreadyMergedConfigurations, List<Feature> configurationToMerge) {
        Set<Feature> featuresToCheckAgainst = new HashSet<>();
        for (PartialConfiguration pc : alreadyMergedConfigurations) {
            featuresToCheckAgainst.addAll(pc.getFeatures());
        }
        featuresToCheckAgainst.addAll(configurationToMerge);

        if (findExcludes(featuresToCheckAgainst)) {
            return false;
        }
        return requiresFulfilled(featuresToCheckAgainst);
    }

    private boolean findExcludes(Set<Feature> featuresToCheck) {
        //TODO Excludes and Requires are no longer limited to the gaps between segments! this code has to change! SAME FOR THE CREATION of excludes for merging!

        var firstIterationExcludes = this.partiallyCalculatedFeatureModel.crossTreeConstraints.stream().filter(crossTreeConstraint -> crossTreeConstraint.getRelation() == CrossTreeConstraintRelation.EXCLUDES).filter(constraint -> featuresToCheck.contains(constraint.getSource())).toList();
        if (firstIterationExcludes.isEmpty()) return false;

        var secondIterationExcludes = firstIterationExcludes.stream().filter(constraint -> featuresToCheck.contains(constraint.getTarget())).toList();
        return !secondIterationExcludes.isEmpty();
    }

    private boolean requiresFulfilled(Set<Feature> featuresToCheck) {
        //TODO Change Logic to search for parents/children
        var unfulfilledRequires = partiallyCalculatedFeatureModel.crossTreeConstraints.stream().filter(crossTreeConstraint -> crossTreeConstraint.getRelation() == CrossTreeConstraintRelation.REQUIRES).filter(constraint -> featuresToCheck.contains(constraint.getSource()) && !featuresToCheck.contains(constraint.getTarget())).toList();
        return unfulfilledRequires.isEmpty();
    }

    private void addConditionalWeights() {
        for (var constraint : partiallyCalculatedFeatureModel.crossTreeConstraints) {
            if (constraint.getRelation() == CrossTreeConstraintRelation.EXCLUDES) {
                var sourceVertex = combinedGraph.getVertexByIdentifier(constraint.getSource().getName());
                var targetVertex = combinedGraph.getVertexByIdentifier(constraint.getTarget().getName());
                if (sourceVertex != null && targetVertex != null) {
                    sourceVertex.addConditionalWeight(targetVertex);
                }
            } else if (constraint.getRelation() == CrossTreeConstraintRelation.REQUIRES) {
                var sourceVertex = combinedGraph.getVertexByIdentifier(constraint.getSource().getName());
                var targetVertices = partiallyCalculatedFeatureModel.features.stream().filter(x -> x.getParentFeature() == constraint.getTarget().getParentFeature()).toList();

                for (var target : targetVertices) {
                    if (target != constraint.getTarget()) {
                        var targetVertex = combinedGraph.getVertexByIdentifier(target.getName());
                        if (sourceVertex != null && targetVertex != null) {
                            sourceVertex.addConditionalWeight(targetVertex);
                        }
                    }
                }
            }
        }
    }

    private void printGraph(Graph graph) {
        System.out.println(" ");
        System.out.println("GRAPH: ");
        for (var vertex : graph.getAllVertices()) {
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(vertex.getLabel());
            System.out.println("successors: ");
            for (var edge : graph.getOutgoingEdges(vertex)) {
                System.out.print(edge.getDestination().getLabel() + " ");
            }
        }
    }
}