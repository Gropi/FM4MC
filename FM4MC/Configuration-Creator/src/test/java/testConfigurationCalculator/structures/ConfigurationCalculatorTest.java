package testConfigurationCalculator.structures;

import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationCalculator.Structures.PartialConfiguration;
import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.Structures.CrossTreeConstraint;
import FeatureModelReader.Structures.CrossTreeConstraintRelation;
import FeatureModelSlicer.FeatureModelSlicer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationCalculatorTest {
    private static final Logger logger = LogManager.getLogger(ConfigurationCalculatorTest.class);

    @Test
    void testCTCPropagation() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [
                        {
                            "sourceName": "b1",
                            "targetName": "A",
                            "relation": "requires"
                        },
                        {
                            "sourceName": "b2",
                            "targetName": "A",
                            "relation": "excludes"
                        }
                    ],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "A",
                                "B"
                            ],
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "tsv1",
                            "parentName": "startTask",
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "OPTIONAL"
                        },
                        {
                            "name": "B",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "a",
                            "parentName": "A",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "b1",
                            "parentName": "B",
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "b2",
                            "parentName": "B",
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "name": "endTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "tev1",
                            "parentName": "endTask",
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;

        // Write JSON to temporary file
        var tempFile = File.createTempFile("testCTCPropagation", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(tempFile);
        // Assuming that the FeatureModelRead class contains a list "crossTreeConstraints"
        var constraints = model.crossTreeConstraints;
        assertNotNull(constraints, "Constraints list should not be null");
        assertEquals(2, constraints.size(), "There should be 2 cross-tree constraints");

        // Check first constraint
        CrossTreeConstraint c1 = constraints.get(0);
        assertEquals("b1", c1.getSource().getName());
        assertEquals("A", c1.getTarget().getName());
        assertEquals(CrossTreeConstraintRelation.REQUIRES, c1.getRelation());

        // Check second constraint
        CrossTreeConstraint c2 = constraints.get(1);
        assertEquals("b2", c2.getSource().getName());
        assertEquals("A", c2.getTarget().getName());
        assertEquals(CrossTreeConstraintRelation.EXCLUDES, c2.getRelation());

        var slicer = new FeatureModelSlicer(logger);
        var calculator = new ConfigurationCalculator(logger);
        var calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, 1));

        assertEquals(2, calculatedFM.crossTreeConstraints.size());
        assertNotEquals(calculatedFM.crossTreeConstraints.get(0).getRelation(), calculatedFM.crossTreeConstraints.get(1).getRelation());
        for (var constraint : calculatedFM.crossTreeConstraints) {
            switch (constraint.getRelation()) {
                case REQUIRES -> {
                    assertEquals("b1", constraint.getSource().getName(), "The source of the require constraint should be unchanged");
                    assertEquals("A", constraint.getTarget().getName(), "The target of the require constraint should be unchanged");
                }
                case EXCLUDES -> {
                    assertEquals("b2", constraint.getSource().getName(), "The source of the exclude constraint should be unchanged");
                    assertEquals("a", constraint.getTarget().getName(), "The target of the exclude constraint should be passed to the child of A");
                }
            }
        }
        assertEquals(2, calculatedFM.abstractConfigurations.size(), "There should be 2 abstract-configurations: 'Start,B,End' and 'Start,A,B,End'");
        assertEquals(4, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 4 concrete PFMs. start, end, A, B");
        tempFile.delete();
    }

    @Test
    void testLocalCTCRemoval() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [
                        {
                            "sourceName": "B",
                            "targetName": "A",
                            "relation": "requires"
                        },
                        {
                            "sourceName": "C",
                            "targetName": "D",
                            "relation": "excludes"
                        },
                        {
                            "sourceName": "b1",
                            "targetName": "c1",
                            "relation": "excludes"
                        }
                    ],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "A",
                                "B",
                                "D"
                            ],
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "tsv1",
                            "parentName": "startTask",
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "OPTIONAL"
                        },
                        {
                            "name": "B",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "C"
                            ],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "C",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "D",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "OPTIONAL"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "a",
                            "parentName": "A",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "b1",
                            "parentName": "B",
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "b2",
                            "parentName": "B",
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "c1",
                            "parentName": "C",
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "c2",
                            "parentName": "C",
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "d",
                            "parentName": "D",
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "endTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "tev1",
                            "parentName": "endTask",
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;

        // Write JSON to temporary file
        var tempFile = File.createTempFile("testLokalCTCRemoval", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(tempFile);
        // Assuming that the FeatureModelRead class contains a list "crossTreeConstraints"
        var constraints = model.crossTreeConstraints;
        assertNotNull(constraints, "Constraints list should not be null");
        assertEquals(3, constraints.size(), "There should be 3 cross-tree constraints");

        var slicer = new FeatureModelSlicer(logger);
        var calculator = new ConfigurationCalculator(logger);
        var calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, 1));

        assertEquals(1, calculatedFM.crossTreeConstraints.size(), "After calculating partial configurations with a small Threshold there should be 1 CTC left");
        assertEquals(CrossTreeConstraintRelation.EXCLUDES, calculatedFM.crossTreeConstraints.get(0).getRelation());
        assertEquals("b1", calculatedFM.crossTreeConstraints.get(0).getSource().getName());
        assertEquals("c1", calculatedFM.crossTreeConstraints.get(0).getTarget().getName());

        assertEquals(1, calculatedFM.abstractConfigurations.size(), "There is only 1 valid abstract configuration: 'Start,A,B,C,End'");
        assertEquals(6, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 6 PFMs. One for: start, end, A, B, C, D");

        calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, 8));
        assertEquals(0, calculatedFM.crossTreeConstraints.size(), "After calculating partial configurations with a big Threshold there are no CTC left");
        assertEquals(1, calculatedFM.abstractConfigurations.size(), "There is only 1 valid abstract configuration: 'Start,A,B,C,End'");
        assertEquals(5, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 5 PFMs. One for: start, end, A, BC, D");


        tempFile.delete();
    }

    @Test
    void testLocalRequireCTC() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForLocalRequireCTCTest.json");

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);
        // Assuming that the FeatureModelRead class contains a list "crossTreeConstraints"
        var constraints = model.crossTreeConstraints;
        assertNotNull(constraints, "Constraints list should not be null");
        assertEquals(2, constraints.size(), "There should be 2 cross-tree constraints");

        var slicer = new FeatureModelSlicer(logger);
        var calculator = new ConfigurationCalculator(logger);
        var calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, 1));

        assertEquals(2, calculatedFM.crossTreeConstraints.size(), "After calculating partial configurations with a small Threshold there should be 2 CTC left");
        assertEquals(CrossTreeConstraintRelation.REQUIRES, calculatedFM.crossTreeConstraints.get(0).getRelation());
        assertEquals("a1", calculatedFM.crossTreeConstraints.get(0).getSource().getName());
        assertEquals("b1", calculatedFM.crossTreeConstraints.get(0).getTarget().getName());

        assertEquals(CrossTreeConstraintRelation.REQUIRES, calculatedFM.crossTreeConstraints.get(1).getRelation());
        assertEquals("a2", calculatedFM.crossTreeConstraints.get(1).getSource().getName());
        assertEquals("b2", calculatedFM.crossTreeConstraints.get(1).getTarget().getName());

        assertEquals(4, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 4 PFMs. One for: start, end, A, B");

        calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, 4));
        assertEquals(0, calculatedFM.crossTreeConstraints.size(), "After calculating partial configurations with a big Threshold there are no CTC left");
        assertEquals(1, calculatedFM.abstractConfigurations.size(), "There is only 1 valid abstract configuration: 'Start,A,B,End'");
        assertEquals(1, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 1 PFM");
        assertEquals(2, calculatedFM.configurationsPerPartialFeatureModel.get(0).size(), "There should be 2 Configurations in this PFM");

        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.get(0).get(0).getFeatures().stream().anyMatch(f -> f.getName().equals("a1")), "The first configuration should have a feature a1");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.get(0).get(0).getFeatures().stream().anyMatch(f -> f.getName().equals("b1")), "The first configuration should have a feature b1");

        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.get(0).get(1).getFeatures().stream().anyMatch(f -> f.getName().equals("a2")), "The second configuration should have a feature a2");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.get(0).get(1).getFeatures().stream().anyMatch(f -> f.getName().equals("b2")), "The second configuration should have a feature b2");

    }

    @Test
    void testAbstractLayerConfiguration() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForAbstractLayerTesting.json");
        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicer = new FeatureModelSlicer(logger);
        var calculator = new ConfigurationCalculator(logger);
        var calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, 1));

        assertEquals(24, calculatedFM.abstractConfigurations.size(), "There should be 24 abstractConfigurations, 2 from the optional features * 3 from the OR group * 4 from the XOR group");
        var min = calculatedFM.abstractConfigurations.stream().mapToInt(List::size).min().getAsInt();
        var max = calculatedFM.abstractConfigurations.stream().mapToInt(List::size).max().getAsInt();
        assertEquals(5, min, "The smallest configuration has 5 abstract features, 'start, 1 mandatory, 1 OR, 1 XOR, end'");
        assertEquals(7, max, "The largest configuration has 7 abstract features, 'start, 1 mandatory, 1 optional, 2 OR, 1 XOR, end'");
    }

    @Test
    void testConfigurationWithChain() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForSequenceDetectionTest.json");
        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicer = new FeatureModelSlicer(logger);
        var calculator = new ConfigurationCalculator(logger);

        int threshold = 0;
        var calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, threshold));
        assertEquals(14, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 14 PFMs when slicing with a threshold of 0");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.size() <= 2), "The maximum number of configurations in a PFM should be 2");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(list -> list.size() == 2), "At least one PFM should have 2 configurations");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.stream().allMatch(config -> config.getFeatures().size() == 1)), "each configuration should contain exactly one feature");

        threshold = 4;
        calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, threshold));
        assertEquals(8, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 8 PFMs when slicing with a threshold of 4");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.size() <= 4), "The maximum number of configurations in a PFM should be 4");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(list -> list.size() == 4), "At least one PFM should have 4 configurations");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.stream().allMatch(config -> config.getFeatures().size() <= 2)), "each configuration should contain not more than 2 features");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(list -> list.stream().allMatch(config -> config.getFeatures().size() == 2)), "there should be a configuration with 2 features");

        threshold = 8;
        calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, threshold));
        assertEquals(6, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 6 PFMs when slicing with a threshold of 8");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.size() <= 8), "The maximum number of configurations in a PFM should be 8");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(list -> list.size() == 8), "At least one PFM should have 8 configurations");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.stream().allMatch(config -> config.getFeatures().size() <= 3)), "each configuration should contain not more than 3 features");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(list -> list.stream().allMatch(config -> config.getFeatures().size() == 3)), "there should be a configuration with 3 features");

        threshold = 64;
        calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, threshold));
        assertEquals(4, calculatedFM.configurationsPerPartialFeatureModel.size(), "There should be 4 PFMs when slicing with a threshold of 64");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.size() <= 64), "The maximum number of configurations in a PFM should be 64");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(list -> list.size() == 64), "At least one PFM should have 64 configurations");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(list -> list.stream().allMatch(config -> config.getFeatures().size() <= 6)), "each configuration should contain not more than 6 features");
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(list -> list.stream().allMatch(config -> config.getFeatures().size() == 6)), "there should be a configuration with 6 features");


    }

    @Test
    void testNonSlicedFM() throws Exception {
        var readFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json");
        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);

        var configCalc = new ConfigurationCalculator(logger);
        var finalFM = configCalc.calculatedConfigurationForNonSlicedFM(model);

        assertEquals(1, finalFM.abstractConfigurations.size());
        assertEquals(9, finalFM.abstractConfigurations.get(0).size());
        assertEquals(4096, finalFM.configurationsPerPartialFeatureModel.size());
    }

    @Test
    void testNonSlicedDeepFM() throws Exception {
        var readFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json");
        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);

        var configCalc = new ConfigurationCalculator(logger);
        var finalFM = configCalc.calculatedConfigurationForNonSlicedFM(model);

        // 3A xor 3B, 5A OR 5B, 5A2 Optional, 5B = 1, 5A = 2
        //      2   *  (1 + 2 + 2)
        assertEquals(10, finalFM.abstractConfigurations.size());
        assertEquals(9, finalFM.abstractConfigurations.stream().mapToInt(List::size).min().getAsInt());
        assertEquals(12, finalFM.abstractConfigurations.stream().mapToInt(List::size).max().getAsInt());
        assertEquals(37120, finalFM.configurationsPerPartialFeatureModel.size());


    }

    @Test
    void testSlicedFM() throws Exception {
        var readFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json");

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicer = new FeatureModelSlicer(logger);
        var calculator = new ConfigurationCalculator(logger);

        int threshold = 250;
        var calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, threshold));

        assertEquals(1, calculatedFM.abstractConfigurations.size());
        assertEquals(9, calculatedFM.abstractConfigurations.get(0).size());
        assertEquals(7, calculatedFM.configurationsPerPartialFeatureModel.size());
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().map(List::size).allMatch(x -> x <= 4));
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().map(List::size).anyMatch(x -> x == 4));
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(configurations -> configurations.stream().allMatch(config -> config.getFeatures().size() <= 3)));
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(configurations -> configurations.stream().allMatch(config -> config.getFeatures().size() == 3)));
    }

    @Test
    void testSlicedDeepFM() throws Exception {
        var readFile = new File("../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json");

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicer = new FeatureModelSlicer(logger);
        var calculator = new ConfigurationCalculator(logger);

        int threshold = 250;
        var calculatedFM = calculator.calculatePartialConfigurations(slicer.sliceFeatureModel(model, threshold));

        // 3A xor 3B, 5A OR 5B, 5A2 Optional, 5B = 1, 5A = 2
        //      2   *  (1 + 2 + 2)
        assertEquals(10, calculatedFM.abstractConfigurations.size());
        assertEquals(9, calculatedFM.abstractConfigurations.stream().mapToInt(List::size).min().getAsInt());
        assertEquals(12, calculatedFM.abstractConfigurations.stream().mapToInt(List::size).max().getAsInt());
        assertEquals(12, calculatedFM.configurationsPerPartialFeatureModel.size());
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().map(List::size).allMatch(x -> x <= 4));
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().map(List::size).anyMatch(x -> x == 4));
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().allMatch(configurations -> configurations.stream().allMatch(config -> config.getFeatures().size() <= 2)));
        assertTrue(calculatedFM.configurationsPerPartialFeatureModel.stream().anyMatch(configurations -> configurations.stream().allMatch(config -> config.getFeatures().size() == 2)));


    }
}
