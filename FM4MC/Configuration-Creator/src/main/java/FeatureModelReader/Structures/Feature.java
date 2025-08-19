package FeatureModelReader.Structures;

import IO.impl.LshwClass;

import java.util.*;

/**
 * Represents a feature in the feature model including hierarchy information,
 * relations and hardware requirements.
 */
public class Feature {
    private String _name;
    private String _parentFeatureName;
    private Feature _parentFeature;
    private FeatureModelRelation _relation;
    private int _index;
    private Map<LshwClass, Integer> _hardwareRequirements = new HashMap<>();
    private Map<String, Integer> _responseTimes = new HashMap<>();

    // Hierarchical structure: direct children
    private List<Feature> _children;
    private List<Feature> _successiveFeatures;

    /**
     * Creates an empty feature.
     */
    public Feature() {
        _children = new ArrayList<>();
        _successiveFeatures = new ArrayList<>();
    }

    /**
     * Creates a feature with the given name, index and parent.
     */
    public Feature(String name, int index, Feature parentFeature) {
        _name = name;
        _index = index;
        _parentFeature = parentFeature;
        if (parentFeature != null) {
            _parentFeatureName = parentFeature.getName();
        } else {
            _parentFeatureName = "";
        }
        _children = new ArrayList<>();
        _successiveFeatures = new ArrayList<>();
    }

    // Getters and Setters
    /**
     * @return feature name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the feature name.
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * @return name of the parent feature
     */
    public String getParentFeatureName() {
        return _parentFeatureName;
    }

    /**
     * Sets the parent feature name.
     */
    public void setParentFeatureName(String parentFeatureName) {
        _parentFeatureName = parentFeatureName;
    }

    /**
     * @return parent feature instance or {@code null}
     */
    public Feature getParentFeature() {
        return _parentFeature;
    }

    /**
     * Sets the parent feature instance.
     */
    public void setParentFeature(Feature parentFeature) {
        _parentFeature = parentFeature;
    }

    /**
     * @return relation to the parent feature
     */
    public FeatureModelRelation getRelation() {
        return _relation;
    }

    /**
     * Sets the relation to the parent feature.
     */
    public void setRelation(FeatureModelRelation relation) {
        _relation = relation;
    }

    /**
     * @return numerical index used in CNF clauses
     */
    public int getIndex() {
        return _index;
    }

    /**
     * Sets the numerical index.
     */
    public void setIndex(int index) {
        _index = index;
    }

    /**
     * @return required hardware resources
     */
    public Map<LshwClass, Integer> getHardwareRequirements() {
        return _hardwareRequirements;
    }

    /**
     * Sets the required hardware resources.
     */
    public void setHardwareRequirements(Map<LshwClass, Integer> hardwareRequirements) {
        _hardwareRequirements = hardwareRequirements;
    }

    /**
     * @return recorded response times
     */
    public Map<String, Integer> getResponseTimes() {
        return _responseTimes;
    }

    /**
     * Sets recorded response times.
     */
    public void setResponseTimes(Map<String, Integer> responseTimes) {
        _responseTimes = responseTimes;
    }

    /**
     * @return list of direct child features
     */
    public List<Feature> getChildren() {
        return _children;
    }

    /**
     * Sets the list of child features.
     */
    public void setChildren(List<Feature> children) {
        _children = children;
    }

    /**
     * Adds a child feature.
     */
    public void addChild(Feature child) {
        _children.add(child);
    }

    /**
     * @return features that follow this feature in the call graph
     */
    public List<Feature> getSuccessiveFeatures() {
        return _successiveFeatures;
    }

    /**
     * Sets the successive features in the call graph.
     */
    public void setSuccessiveFeatures(List<Feature> successiveFeatures) {
        this._successiveFeatures = successiveFeatures;
    }

    /**
     * Adds a successive feature in the call graph.
     */
    public void addSuccessiveFeature(Feature successiveFeature) {
        _successiveFeatures.add(successiveFeature);
    }


    /**
     * @return string representation mainly for debugging
     */
    @Override
    public String toString() {
        return "Feature{" +
                "name='" + _name + '\'' +
                ", relation=" + _relation +
                ", index=" + _index +
                ", children=" + _children +
                '}';
    }

    /**
     * Compares features by their name which acts as unique identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feature feature)) return false;
        return Objects.equals(_name, feature._name);
    }

    /**
     * Hashes by feature name.
     */
    @Override
    public int hashCode() {
        return Objects.hash(_name);
    }


}