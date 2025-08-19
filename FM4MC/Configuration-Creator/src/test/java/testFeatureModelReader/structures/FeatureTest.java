package testFeatureModelReader.structures;

import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRelation;
import IO.impl.LshwClass;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureTest {

    @Test
    public void testGettersAndSetters() {
        Feature feature = new Feature();
        feature.setName("TestFeature");
        feature.setParentFeatureName("ParentFeature");
        feature.setIndex(10);
        feature.setRelation(FeatureModelRelation.MANDATORY);

        Map<LshwClass, Integer> hardwareReq = new HashMap<>();
        hardwareReq.put(LshwClass.ENERGY, 2);
        feature.setHardwareRequirements(hardwareReq);

        Map<String, Integer> responseTimes = new HashMap<>();
        responseTimes.put("min", 100);
        feature.setResponseTimes(responseTimes);

        // Überprüfe die Getter
        assertEquals("TestFeature", feature.getName());
        assertEquals("ParentFeature", feature.getParentFeatureName());
        assertEquals(10, feature.getIndex());
        assertEquals(FeatureModelRelation.MANDATORY, feature.getRelation());
        assertEquals(2, feature.getHardwareRequirements().get(LshwClass.ENERGY));
        assertEquals(100, feature.getResponseTimes().get("min").intValue());
    }

    @Test
    public void testChildrenHandling() {
        Feature parent = new Feature("ParentFeature", 1, null);
        Feature child1 = new Feature("Child1", 2, parent);
        Feature child2 = new Feature("Child2", 3, parent);

        parent.addChild(child1);
        parent.addChild(child2);

        List<Feature> children = parent.getChildren();
        assertNotNull(children);
        assertEquals(2, children.size());
        assertEquals("Child1", children.get(0).getName());
        assertEquals("Child2", children.get(1).getName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Create two features with the same name and check equals and hashCode.
        Feature feature1 = new Feature();
        feature1.setName("TestFeature");
        feature1.setIndex(1);

        Feature feature2 = new Feature();
        feature2.setName("TestFeature");
        feature2.setIndex(2); // Different index but equals() is based on name

        assertEquals(feature1, feature2, "Features with the same name should be equal");
        assertEquals(feature1.hashCode(), feature2.hashCode(), "Hash codes should be equal for equal features");

        // Create a third feature with a different name and verify inequality.
        Feature feature3 = new Feature();
        feature3.setName("AnotherFeature");
        feature3.setIndex(1);

        assertNotEquals(feature1, feature3, "Features with different names should not be equal");
    }
}
