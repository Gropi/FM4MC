package FeatureModelReader.Structures;

public class CrossTreeConstraint {

    private Feature _source;
    private Feature _target;
    private CrossTreeConstraintRelation _relation;

    public CrossTreeConstraint(Feature source, Feature target, CrossTreeConstraintRelation relation) {
        _source = source;
        _target = target;
        _relation = relation;
    }

    public Feature getSource() {
        return _source;
    }

    public Feature getTarget() {
        return _target;
    }

    public CrossTreeConstraintRelation getRelation() {
        return _relation;
    }
}
