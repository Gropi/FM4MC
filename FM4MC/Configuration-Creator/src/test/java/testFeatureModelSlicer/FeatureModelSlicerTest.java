package testFeatureModelSlicer;

import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.FeatureModelSlicer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeatureModelSlicerTest {
    private static final Logger logger = LogManager.getLogger(FeatureModelSlicerTest.class);

    @Test
    void testSlicingAbstractLayer() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForAbstractLayerTesting.json");
        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicer = new FeatureModelSlicer(logger);
        int threshold = 0;
        var slicedFM = slicer.sliceFeatureModel(model, threshold);
        assertEquals(23, slicedFM.features.size());
        assertEquals(13, slicedFM.abstractLayerFeatureModels.size());
        assertEquals(10, slicedFM.partialConcreteFeatureModels.size());
        assertEquals(model.features.size(), slicedFM.features.size());
        assertEquals(slicedFM.features.size(), slicedFM.abstractLayerFeatureModels.size() + slicedFM.partialConcreteFeatureModels.size());
        assertEquals(10, slicedFM.featureConnectivityInformation.featureConnectivityMap.size());
    }

    @Test
    void testSequenceDetection() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForSequenceDetectionTest.json");
        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicer = new FeatureModelSlicer(logger);
        int threshold = 0;
        var slicedFM = slicer.sliceFeatureModel(model, threshold);
        assertEquals(41, slicedFM.features.size());
        assertEquals(15, slicedFM.abstractLayerFeatureModels.size());
        assertEquals(14, slicedFM.partialConcreteFeatureModels.size());
        threshold = 4;
        slicedFM = slicer.sliceFeatureModel(model, threshold);
        assertEquals(8, slicedFM.partialConcreteFeatureModels.size());
        threshold = 8;
        slicedFM = slicer.sliceFeatureModel(model, threshold);
        assertEquals(6, slicedFM.partialConcreteFeatureModels.size());
        threshold = 64;
        slicedFM = slicer.sliceFeatureModel(model, threshold);
        assertEquals(4, slicedFM.partialConcreteFeatureModels.size());
    }

    @Test
    void testSlicerCrossTreeConstraintsAndHardwareRequirements() throws Exception {
        // Test JSON with cross-tree constraints and hardware requirements.
        var json = """
                {
                    "crossTreeConstraints": [
                        {
                            "sourceName": "A",
                            "targetName": "B",
                            "relation": "requires"
                        }
                    ],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "B",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "a",
                            "parentName": "A",
                            "relation": "MANDATORY",
                            "hardwareRequirements": [
                                { "hardwareType": "PROCESSOR", "requirement": 4 },
                                { "hardwareType": "MEMORY", "requirement": 8 }
                            ]
                        },
                        {
                            "name": "b",
                            "parentName": "B",
                            "relation": "MANDATORY",
                            "hardwareRequirements": [
                                { "hardwareType": "PROCESSOR", "requirement": 2 }
                            ]
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("testSlicerConstraintsHW", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        FeatureModelRead model = reader.readFeatureModelJson(tempFile);
        // Check that cross-tree constraint is parsed
        assertNotNull(model.crossTreeConstraints, "crossTreeConstraints should not be null");
        assertEquals(1, model.crossTreeConstraints.size(), "There should be 1 cross-tree constraint");
        var ctc = model.crossTreeConstraints.get(0);
        assertEquals("A", ctc.getSource().getName(), "Constraint source should be A");
        assertEquals("B", ctc.getTarget().getName(), "Constraint target should be B");

        // Check hardware requirements of feature A
        var feature_a = model.features.stream().filter(f -> f.getName().equals("a")).findFirst().orElse(null);
        assertNotNull(feature_a, "Feature a should exist");
        var hwReqA = feature_a.getHardwareRequirements();
        assertEquals(4, hwReqA.getOrDefault(IO.impl.LshwClass.PROCESSOR, 0));
        assertEquals(8, hwReqA.getOrDefault(IO.impl.LshwClass.MEMORY, 0));

        tempFile.delete();
    }
}
