package FeatureModelReader.Structures;

import IO.impl.LshwClass;

import java.util.*;

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

    public Feature() {
        _children = new ArrayList<>();
        _successiveFeatures = new ArrayList<>();
    }

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
    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getParentFeatureName() {
        return _parentFeatureName;
    }

    public void setParentFeatureName(String parentFeatureName) {
        _parentFeatureName = parentFeatureName;
    }

    public Feature getParentFeature() {
        return _parentFeature;
    }

    public void setParentFeature(Feature parentFeature) {
        _parentFeature = parentFeature;
    }

    public FeatureModelRelation getRelation() {
        return _relation;
    }

    public void setRelation(FeatureModelRelation relation) {
        _relation = relation;
    }

    public int getIndex() {
        return _index;
    }

    public void setIndex(int index) {
        _index = index;
    }

    public Map<LshwClass, Integer> getHardwareRequirements() {
        return _hardwareRequirements;
    }

    public void setHardwareRequirements(Map<LshwClass, Integer> hardwareRequirements) {
        _hardwareRequirements = hardwareRequirements;
    }

    public Map<String, Integer> getResponseTimes() {
        return _responseTimes;
    }

    public void setResponseTimes(Map<String, Integer> responseTimes) {
        _responseTimes = responseTimes;
    }

    public List<Feature> getChildren() {
        return _children;
    }

    public void setChildren(List<Feature> children) {
        _children = children;
    }

    public void addChild(Feature child) {
        _children.add(child);
    }

    public List<Feature> getSuccessiveFeatures() {
        return _successiveFeatures;
    }

    public void setSuccessiveFeatures(List<Feature> successiveFeatures) {
        this._successiveFeatures = successiveFeatures;
    }

    public void addSuccessiveFeature(Feature successiveFeature) {
        _successiveFeatures.add(successiveFeature);
    }


    @Override
    public String toString() {
        return "Feature{" +
                "name='" + _name + '\'' +
                ", relation=" + _relation +
                ", index=" + _index +
                ", children=" + _children +
                '}';
    }

    // Override equals and hashCode based on the name (unique identifier)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feature feature)) return false;
        return Objects.equals(_name, feature._name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name);
    }


}