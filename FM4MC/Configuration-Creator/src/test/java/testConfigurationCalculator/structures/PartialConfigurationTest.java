package testConfigurationCalculator.structures;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PartialConfiguration class.
 */
class PartialConfigurationTest {

    private PartialConfiguration config;
    private Feature feature1;
    private Feature feature2;

    @BeforeEach
    void setUp() {
        // Assuming Feature has a suitable constructor
        feature1 = new Feature("Feature1", 1, null);
        feature2 = new Feature("Feature2", 2, null);
        config = new PartialConfiguration();
    }

    /**
     * Test that the default constructor initializes an empty feature list.
     */
    @Test
    void testDefaultConstructor() {
        assertNotNull(config.getFeatures());
        assertTrue(config.getFeatures().isEmpty());
    }

    /**
     * Test the constructor that takes a list of features.
     */
    @Test
    void testConstructorWithList() {
        var features = Arrays.asList(feature1, feature2);
        var pc = new PartialConfiguration(features);
        assertEquals(2, pc.getFeatures().size());
        assertTrue(pc.getFeatures().contains(feature1));
        assertTrue(pc.getFeatures().contains(feature2));
    }

    /**
     * Test adding a feature to the configuration.
     */
    @Test
    void testAddFeature() {
        config.addFeature(feature1);
        assertEquals(1, config.getFeatures().size());
        assertTrue(config.getFeatures().contains(feature1));
    }

    /**
     * Test removing a feature from the configuration.
     */
    @Test
    void testRemoveFeature() {
        config.addFeature(feature1);
        config.addFeature(feature2);
        var removed = config.removeFeature(feature1);
        assertTrue(removed);
        assertEquals(1, config.getFeatures().size());
        assertFalse(config.getFeatures().contains(feature1));
    }

    /**
     * Test setting a new list of features.
     */
    @Test
    void testSetFeatures() {
        var newFeatures = Arrays.asList(feature1);
        config.setFeatures(newFeatures);
        assertEquals(1, config.getFeatures().size());
        assertTrue(config.getFeatures().contains(feature1));

        // Setting null should result in an empty list.
        config.setFeatures(null);
        assertNotNull(config.getFeatures());
        assertTrue(config.getFeatures().isEmpty());
    }

    /**
     * Test that the getter returns an unmodifiable list.
     */
    @Test
    void testImmutabilityOfGetter() {
        config.addFeature(feature1);
        var featuresFromGetter = config.getFeatures();
        // Attempting to modify the unmodifiable list should throw an exception.
        assertThrows(UnsupportedOperationException.class, () -> featuresFromGetter.add(feature2));
    }
}
