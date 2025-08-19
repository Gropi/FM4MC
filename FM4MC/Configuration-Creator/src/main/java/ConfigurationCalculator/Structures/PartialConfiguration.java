package ConfigurationCalculator.Structures;

import FeatureModelReader.Structures.Feature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a partial configuration composed of a list of features.
 * Provides methods to get, set, add, and remove features.
 */
public class PartialConfiguration {
    /**
     * The list of features in the partial configuration.
     */
    private List<Feature> _Features;
    private List<Feature> _AbstractParents;

    /**
     * Default constructor that initializes an empty feature list.
     */
    public PartialConfiguration() {
        _Features = new ArrayList<>();
        _AbstractParents = new ArrayList<>();
    }

    /**
     * Constructs a PartialConfiguration with the given list of features.
     *
     * @param features the list of features to initialize the configuration with;
     *                 if null, an empty list is used.
     */
    public PartialConfiguration(List<Feature> features) {
        _Features = (features != null) ? new ArrayList<>(features) : new ArrayList<>();
    }

    /**
     * Returns an unmodifiable view of the feature list.
     *
     * @return an unmodifiable list of features.
     */
    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(_Features);
    }

    /**
     * Sets the feature list to a copy of the provided list.
     *
     * @param features the new list of features; if null, an empty list is used.
     */
    public void setFeatures(List<Feature> features) {
        _Features = (features != null) ? new ArrayList<>(features) : new ArrayList<>();
    }

    /**
     * Adds a single feature to the configuration.
     *
     * @param feature the feature to add; if null, the method does nothing.
     */
    public void addFeature(Feature feature) {
        if (feature != null) {
            _Features.add(feature);
        }
    }

    /**
     * Removes a feature from the configuration.
     *
     * @param feature the feature to remove.
     * @return true if the feature was removed, false otherwise.
     */
    public boolean removeFeature(Feature feature) {
        return _Features.remove(feature);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartialConfiguration)) return false;
        PartialConfiguration that = (PartialConfiguration) o;
        return Objects.equals(_Features, that._Features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_Features);
    }

    @Override
    public String toString() {
        return "PartialConfiguration{" +
                "features=" + _Features +
                '}';
    }

    /**
     * Returns the abstract parent features associated with this configuration.
     */
    public List<Feature> getAbstractParent() {
        return _AbstractParents;
    }

    /**
     * Sets the abstract parent features of this configuration.
     *
     * @param abstractParents list of abstract parent features
     */
    public void setAbstractParent(List<Feature> abstractParents) {
        this._AbstractParents = abstractParents;
    }

    /**
     * Adds an abstract parent feature to this configuration.
     *
     * @param abstractParent parent feature to add
     */
    public void addAbstractParent(Feature abstractParent) {
        _AbstractParents.add(abstractParent);
    }
}
