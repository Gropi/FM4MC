package FeatureModelReader.Structures;

/**
 * Represents a cross-tree constraint between two features.
 */
public class CrossTreeConstraint {

    private Feature _source;
    private Feature _target;
    private CrossTreeConstraintRelation _relation;

    /**
     * Creates a new cross-tree constraint.
     *
     * @param source   source feature
     * @param target   target feature
     * @param relation relation type
     */
    public CrossTreeConstraint(Feature source, Feature target, CrossTreeConstraintRelation relation) {
        _source = source;
        _target = target;
        _relation = relation;
    }

    /**
     * @return source feature of the constraint
     */
    public Feature getSource() {
        return _source;
    }

    /**
     * @return target feature of the constraint
     */
    public Feature getTarget() {
        return _target;
    }

    /**
     * @return constraint relation
     */
    public CrossTreeConstraintRelation getRelation() {
        return _relation;
    }
}
