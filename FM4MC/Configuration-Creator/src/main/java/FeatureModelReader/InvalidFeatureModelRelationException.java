package FeatureModelReader;

/**
 * Exception thrown when an unknown feature relation is encountered while
 * reading the feature model.
 */
public class InvalidFeatureModelRelationException extends Exception {
    /**
     * Creates the exception with a descriptive message.
     */
    public InvalidFeatureModelRelationException(String message) {
        super(message);
    }
}
