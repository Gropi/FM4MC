package FeatureModelReader;

import FeatureModelReader.Structures.*;
import IO.impl.LshwClass;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class FeatureModelReader {
    private final Logger _Logger;
    private Map<String, List<String>> _FeatureConnectivityMap;
    public static final String ROOT_FEATURE_NAME = "root";
    public final Gson _GsonReader;

    /**
     * Creates a new reader instance.
     *
     * @param logger logger used for diagnostic messages
     */
    public FeatureModelReader(Logger logger) {
        _Logger = logger;
        _GsonReader = new Gson();
    }

    /**
     * Reads a feature model definition from the given JSON file.
     *
     * @param file JSON file representing a feature model
     * @return parsed feature model representation or {@code null} if an error occurs
     * @throws InvalidFeatureModelRelationException if the model contains invalid
     *                                              relations
     */
    public FeatureModelRead readFeatureModelJson(File file) throws InvalidFeatureModelRelationException {
        _FeatureConnectivityMap = new HashMap<>();
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (Exception e) {
            _Logger.error(e);
            return null;
        }
        var featureModelJsonObject = _GsonReader.fromJson(fileReader, JsonObject.class);
        var featuresJsonArray = featureModelJsonObject.getAsJsonArray("features");
        var crossTreeConstraintsJsonArray = featureModelJsonObject.getAsJsonArray("crossTreeConstraints");

        var rawFeatureModel = new FeatureModelRead();

        // Create Root-Feature
        var rootFeature = new Feature(ROOT_FEATURE_NAME, 1, null);
        rootFeature.setRelation(FeatureModelRelation.MANDATORY);

        var features = new ArrayList<Feature>();
        features.add(rootFeature);

        int nextIndex = 2;
        // Read all Features
        for (var featureElement : featuresJsonArray) {
            var featureObject = featureElement.getAsJsonObject();
            var feature = new Feature();
            feature.setName(featureObject.get("name").getAsString());

            // Check for "parentName": if missing or null, set as null.
            if (featureObject.has("parentName") && !featureObject.get("parentName").isJsonNull()) {
                feature.setParentFeatureName(featureObject.get("parentName").getAsString());
            } else {
                feature.setParentFeatureName(null);
            }

            feature.setIndex(nextIndex++);

            var relationStr = featureObject.get("relation").getAsString();
            switch (relationStr) {
                case "MANDATORY" -> feature.setRelation(FeatureModelRelation.MANDATORY);
                case "OPTIONAL" -> feature.setRelation(FeatureModelRelation.OPTIONAL);
                case "ALTERNATIVE" -> feature.setRelation(FeatureModelRelation.ALTERNATIVE);
                case "OR" -> feature.setRelation(FeatureModelRelation.OR);
                default ->
                        throw new InvalidFeatureModelRelationException("\"" + relationStr + "\" is not a valid relation");
            }

            // Process reachableAbstractFeatures if available
            if (featureObject.has("reachableAbstractFeatures") && !featureObject.get("reachableAbstractFeatures").isJsonNull()) {
                var reachableArray = featureObject.getAsJsonArray("reachableAbstractFeatures");
                var reachableList = new ArrayList<String>();
                for (var abstractFeatureElem : reachableArray) {
                    reachableList.add(abstractFeatureElem.getAsString());
                }
                _FeatureConnectivityMap.put(feature.getName(), reachableList);
            }

            // Read hardwareRequirements
            feature.setHardwareRequirements(readHardwareRequirements(featureObject.get("hardwareRequirements")));

            features.add(feature);
        }

        // Set parent relationships based on parentName
        for (var feature : features) {
            if (feature.getParentFeatureName() != null) {
                if (feature.getParentFeatureName().equals(ROOT_FEATURE_NAME)) {
                    feature.setParentFeature(rootFeature);
                    rootFeature.addChild(feature);
                } else {
                    var parent = getFeatureByName(features, feature.getParentFeatureName());
                    feature.setParentFeature(parent);
                }
            }
        }

        // Build hierarchical structure: First explicitly via parentName
        for (var feature : features) {
            if (feature.getParentFeature() != null) {
                var parent = feature.getParentFeature();
                if (!parent.getChildren().contains(feature)) {
                    parent.addChild(feature);
                }
            }
        }
        // If necessary, add further connections from _FeatureConnectivityMap
        buildHierarchyFromConnectivity(features);
        checkValidRelations(features);

        rawFeatureModel.features = features;
        rawFeatureModel.crossTreeConstraints = readCrossTreeConstraints(crossTreeConstraintsJsonArray, rawFeatureModel.features);
        var featureConnectivityMap = mapConnectivityInformationToFeatures(rawFeatureModel.features);
        var abstractStartFeature = getAbstractStartFeature(featureConnectivityMap, rawFeatureModel.features);
        var startFeature = rawFeatureModel.features.stream().filter(x -> x.getParentFeature() == abstractStartFeature).findFirst().orElse(null);
        rawFeatureModel.featureConnectivityInformation = new FeatureConnectivityInformation(featureConnectivityMap, abstractStartFeature, startFeature);

        return rawFeatureModel;
    }

    /**
     * Validates that all relations between features adhere to the expected
     * constraints of the feature model.
     *
     * @param features list of features to validate
     * @throws InvalidFeatureModelRelationException if the relations are invalid
     */
    private void checkValidRelations(List<Feature> features) throws InvalidFeatureModelRelationException {
        for (var feature : features) {
            if (feature.getParentFeature() == null && !feature.getName().equals(ROOT_FEATURE_NAME)) {
                throw new InvalidFeatureModelRelationException("\"" + feature.getName() + "\" is not root and has no parent");
            }
            if (!feature.getChildren().isEmpty()) {
                if (feature.getChildren().stream().anyMatch(child -> child.getRelation() == FeatureModelRelation.ALTERNATIVE || child.getRelation() == FeatureModelRelation.OR)) {
                    if (feature.getChildren().size() < 2) {
                        throw new InvalidFeatureModelRelationException("\"" + feature.getName() + "\" is parent of a feature group with less than 2 children");
                    }
                    if (!feature.getChildren().stream().allMatch(child -> child.getRelation() == FeatureModelRelation.ALTERNATIVE) && !feature.getChildren().stream().allMatch(child -> child.getRelation() == FeatureModelRelation.OR)) {
                        throw new InvalidFeatureModelRelationException("\"" + feature.getName() + "\" is parent of a mixed feature group");
                    }
                }
                //if the feature is an abstract parent of concrete features
                if (feature.getChildren().stream().anyMatch(child -> child.getChildren().isEmpty())) {
                    if (!feature.getChildren().stream().allMatch(child -> child.getChildren().isEmpty())) {
                        throw new InvalidFeatureModelRelationException("\"" + feature.getName() + "\" has concrete and abstract children");
                    }
                    if (feature.getChildren().size() == 1) {
                        if (feature.getChildren().getFirst().getRelation() != FeatureModelRelation.MANDATORY) {
                            throw new InvalidFeatureModelRelationException("\"" + feature.getName() + "\" has exactly one concrete child but this child is not MANDATORY");
                        }
                    } else if (!feature.getChildren().stream().allMatch(child -> child.getRelation() == FeatureModelRelation.ALTERNATIVE)) {
                        throw new InvalidFeatureModelRelationException("\"" + feature.getName() + "\" has concrete children but some are not ALTERNATIVE");
                    }
                }
            }
        }
    }

    /**
     * Adds hierarchy information based on the connectivity map gathered while
     * reading the feature model.
     *
     * @param features list of all features of the model
     */
    private void buildHierarchyFromConnectivity(List<Feature> features) {
        for (var featureName : _FeatureConnectivityMap.keySet()) {
            var feature = getFeatureByName(features, featureName);
            if (feature != null) {
                var reachable = _FeatureConnectivityMap.get(featureName);
                for (var successorName : reachable) {
                    var succeedingFeature = getFeatureByName(features, successorName);
                    if (succeedingFeature != null && !feature.getSuccessiveFeatures().contains(succeedingFeature)) {
                        feature.addSuccessiveFeature(succeedingFeature);
                    }
                }
            }
        }
    }

    /**
     * Parses the hardware requirements from the JSON representation of a
     * feature.
     *
     * @param hardwareRequirements JSON element describing hardware requirements
     * @return map containing the required values for each hardware class
     */
    private Map<LshwClass, Integer> readHardwareRequirements(JsonElement hardwareRequirements) {
        var hardwareRequirementsMap = new HashMap<LshwClass, Integer>();
        for (var lshwClass : LshwClass.values()) {
            hardwareRequirementsMap.put(lshwClass, 0);
        }
        if (hardwareRequirements != null && !hardwareRequirements.isJsonNull()) {
            var hardwareRequirementsArray = hardwareRequirements.getAsJsonArray();
            try {
                for (var hardwareRequirementsElement : hardwareRequirementsArray) {
                    var hardwareRequirementsObject = hardwareRequirementsElement.getAsJsonObject();
                    if (hardwareRequirementsObject.get("hardwareType") != null) {
                        var lshwClass = LshwClass.valueOf(hardwareRequirementsObject.get("hardwareType").getAsString());
                        int requiredValue = hardwareRequirementsObject.get("requirement").getAsInt();
                        hardwareRequirementsMap.put(lshwClass, requiredValue);
                    }
                }
            } catch (Exception e) {
                _Logger.error(e);
            }
        }
        return hardwareRequirementsMap;
    }

    /**
     * Reads cross-tree constraints from the given JSON array and maps them to
     * feature instances.
     *
     * @param crossTreeConstraintsJsonArray JSON array containing the constraints
     * @param features                      list of all features of the model
     * @return list of parsed cross-tree constraints
     * @throws InvalidFeatureModelRelationException if an unknown relation type is encountered
     */
    private List<CrossTreeConstraint> readCrossTreeConstraints(JsonArray crossTreeConstraintsJsonArray, List<Feature> features) throws InvalidFeatureModelRelationException {
        var crossTreeConstraints = new ArrayList<CrossTreeConstraint>();
        for (var crossTreeConstraintElement : crossTreeConstraintsJsonArray) {
            var rawCrossTreeConstraint = crossTreeConstraintElement.getAsJsonObject();
            var source = getFeatureByName(features, rawCrossTreeConstraint.get("sourceName").getAsString());
            var target = getFeatureByName(features, rawCrossTreeConstraint.get("targetName").getAsString());
            var relationString = rawCrossTreeConstraint.get("relation").getAsString();
            CrossTreeConstraintRelation relation;
            switch (relationString) {
                case "requires" -> relation = CrossTreeConstraintRelation.REQUIRES;
                case "excludes" -> relation = CrossTreeConstraintRelation.EXCLUDES;
                default ->
                        throw new InvalidFeatureModelRelationException("\"" + relationString + "\" is not a valid cross tree constraint");
            }
            crossTreeConstraints.add(new CrossTreeConstraint(source, target, relation));
        }
        return crossTreeConstraints;
    }

    /**
     * Retrieves a feature by its name from the provided feature list.
     *
     * @param features    list of features to search in
     * @param featureName name of the feature to find
     * @return the matching feature or {@code null} if it does not exist
     */
    private Feature getFeatureByName(List<Feature> features, String featureName) {
        return features.stream().filter(x -> x.getName().equals(featureName)).findFirst().orElse(null);
    }

    /**
     * Converts the connectivity information read from the JSON file into actual
     * feature references.
     *
     * @param features list of all features in the model
     * @return map assigning each feature to its reachable abstract features
     */
    private Map<String, List<Feature>> mapConnectivityInformationToFeatures(List<Feature> features) {
        var featureConnectivityMap = new HashMap<String, List<Feature>>();
        for (var key : _FeatureConnectivityMap.keySet()) {
            var abstractFeatures = new ArrayList<Feature>();
            features.stream()
                    .filter(x -> _FeatureConnectivityMap.get(key).contains(x.getName()))
                    .forEach(abstractFeatures::add);
            featureConnectivityMap.put(key, abstractFeatures);
        }
        return featureConnectivityMap;
    }

    /**
     * Determines the initial abstract feature from which the configuration
     * process should start.
     *
     * @param featureConnectivityMap connectivity information between features
     * @param features               list of all features
     * @return the abstract start feature or {@code null} if none can be determined
     */
    private Feature getAbstractStartFeature(Map<String, List<Feature>> featureConnectivityMap, List<Feature> features) {
        var featuresWithOutgoing = featureConnectivityMap.keySet();
        var featuresWithIncoming = new HashSet<String>();
        for (var list : featureConnectivityMap.values()) {
            for (var f : list) {
                featuresWithIncoming.add(f.getName());
            }
        }
        for (var featureName : featuresWithOutgoing) {
            if (!featuresWithIncoming.contains(featureName)) {
                return getFeatureByName(features, featureName);
            }
        }
        return null;
    }
}
