package testFeatureModelReader;

import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.CrossTreeConstraint;
import FeatureModelReader.Structures.CrossTreeConstraintRelation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

class FeatureModelReaderTest {
    private static final Logger logger = LogManager.getLogger(FeatureModelReaderTest.class);

    @Test
    void testReadFeatureModelJsonWithHierarchy() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["task1", "task2", "task3", "task4", "task5"],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "task1",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["task6"],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "task2",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["task6"],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "task3",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["task6", "task3_subgroup"],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "task4",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["task6"],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "task5",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["task6", "task5_subgroup"],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "task6",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["task7"],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "task7",
                            "parentName": "root",
                            "reachableAbstractFeatures": ["endTask"],
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
                            "name": "tsv1",
                            "parentName": "startTask",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "t1v1",
                            "parentName": "task1",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "t2v1",
                            "parentName": "task2",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "t3v1",
                            "parentName": "task3",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "t4v1",
                            "parentName": "task4",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "t5v1",
                            "parentName": "task5",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "t6v1",
                            "parentName": "task6",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "t7v1",
                            "parentName": "task7",
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

        // Write JSON to a temporary file
        var tempFile = File.createTempFile("testFeatureModel", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(tempFile);
        var features = model.features;

        // Check that "startTask" exists
        var startTask = features.stream().filter(f -> f.getName().equals("startTask")).findFirst().orElse(null);
        assertNotNull(startTask, "startTask should exist");

        // startTask should have 5 successors: 5 from reachableAbstractFeatures ("task1" ... "task5")
        var successors = startTask.getSuccessiveFeatures();
        assertEquals(5, successors.size(), "startTask should have 5 successors");
        assertTrue(successors.stream().anyMatch(f -> f.getName().equals("task1")));
        assertTrue(successors.stream().anyMatch(f -> f.getName().equals("task2")));
        assertTrue(successors.stream().anyMatch(f -> f.getName().equals("task3")));
        assertTrue(successors.stream().anyMatch(f -> f.getName().equals("task4")));
        assertTrue(successors.stream().anyMatch(f -> f.getName().equals("task5")));

        var children = startTask.getChildren();
        assertEquals(1, children.size(), "startTask should have 1 child");
        assertTrue(children.stream().anyMatch(f -> f.getName().equals("tsv1")));

        // Check that "tsv1" (with parentName "startTask") is among startTask's successors
        var tsv1 = features.stream().filter(f -> f.getName().equals("tsv1")).findFirst().orElse(null);
        assertNotNull(tsv1, "tsv1 should exist");
        assertTrue(startTask.getChildren().contains(tsv1), "startTask should contain tsv1 as a child");

        tempFile.delete();
    }

    @Test
    void testCrossTreeConstraintsParsing() throws Exception {
        // Sample JSON including cross-tree constraints
        var json = """
                {
                    "crossTreeConstraints": [
                        {
                            "sourceName": "A",
                            "targetName": "B",
                            "relation": "requires"
                        },
                        {
                            "sourceName": "C",
                            "targetName": "D",
                            "relation": "excludes"
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
                            "name": "C",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "D",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
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
                            "name": "b",
                            "parentName": "B",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "c",
                            "parentName": "C",
                            "relation": "MANDATORY"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "d",
                            "parentName": "D",
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;

        // Write JSON to temporary file
        var tempFile = File.createTempFile("testFeatureModelConstraints", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(tempFile);
        // Assuming that the FeatureModelRead class contains a list "crossTreeConstraints"
        var constraints = model.crossTreeConstraints;
        assertNotNull(constraints, "Constraints list should not be null");
        assertEquals(2, constraints.size(), "There should be 2 cross-tree constraints");

        // Check first constraint: A requires B
        CrossTreeConstraint c1 = constraints.get(0);
        assertEquals("A", c1.getSource().getName());
        assertEquals("B", c1.getTarget().getName());
        assertEquals(CrossTreeConstraintRelation.REQUIRES, c1.getRelation());

        // Check second constraint: C excludes D
        CrossTreeConstraint c2 = constraints.get(1);
        assertEquals("C", c2.getSource().getName());
        assertEquals("D", c2.getTarget().getName());
        assertEquals(CrossTreeConstraintRelation.EXCLUDES, c2.getRelation());

        tempFile.delete();
    }

    @Test
    void testHardwareRequirementsParsing() throws Exception {
        // Sample JSON including a feature with hardware requirements
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY",
                            "hardwareRequirements": [
                                { "hardwareType": "PROCESSOR", "requirement": 4 },
                                { "hardwareType": "MEMORY", "requirement": 8 }
                            ]
                        }
                    ]
                }
                """;

        // Write JSON to a temporary file
        var tempFile = File.createTempFile("testHardwareRequirements", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var model = reader.readFeatureModelJson(tempFile);
        var features = model.features;
        // Find feature "A"
        var featureA = features.stream().filter(f -> f.getName().equals("A")).findFirst().orElse(null);
        assertNotNull(featureA, "Feature A should exist");
        var hwRequirements = featureA.getHardwareRequirements();
        assertNotNull(hwRequirements, "Hardware requirements should not be null");
        // Check that the requirements for PROCESSOR and MEMORY are set correctly
        assertEquals(4, hwRequirements.getOrDefault(IO.impl.LshwClass.PROCESSOR, 0));
        assertEquals(8, hwRequirements.getOrDefault(IO.impl.LshwClass.MEMORY, 0));

        tempFile.delete();
    }

    @Test
    void testReadFeatureModelWithNullParentName() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "orphanTask",
                            "parentName": null,
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;

        var tempFile = File.createTempFile("testNullParent", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"orphanTask\" is not root and has no parent", exception.getMessage());
        tempFile.delete();
    }


    @Test
    void testOptionalConcreteFeature() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "a",
                            "parentName": A,
                            "relation": "OPTIONAL"
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("testOptionalConcreteFeature", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"A\" has exactly one concrete child but this child is not MANDATORY", exception.getMessage());
        tempFile.delete();
    }

    @Test
    void testORConcreteFeatures() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "a1",
                            "parentName": A,
                            "relation": "OR"
                        },
                        {
                            "name": "a2",
                            "parentName": A,
                            "relation": "OR"
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("testORConcreteFeatures", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"A\" has concrete children but some are not ALTERNATIVE", exception.getMessage());
        tempFile.delete();
    }

    @Test
    void testMixingConcreteAndAbstractFeatures() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "B",
                            "parentName": A,
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "a",
                            "parentName": A,
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "b",
                            "parentName": B,
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("testConcrete+Abstract", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"A\" has concrete and abstract children", exception.getMessage());
        tempFile.delete();
    }

    @Test
    void testMixingFeatureGroups() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "OR"
                        },
                        {
                            "name": "B",
                            "parentName": root,
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "name": "a",
                            "parentName": A,
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "b",
                            "parentName": B,
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("testMixingFeatureGroups", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"root\" is parent of a mixed feature group", exception.getMessage());
        tempFile.delete();
    }

    @Test
    void testSmallFeatureGroup() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "OR"
                        },
                        {
                            "name": "a",
                            "parentName": A,
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("smallFeatureGroup", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"root\" is parent of a feature group with less than 2 children", exception.getMessage());
        tempFile.delete();
    }

    @Test
    void testInvalidRelation() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "A",
                            "parentName": "root",
                            "reachableAbstractFeatures": [],
                            "relation": "INVALID"
                        },
                        {
                            "name": "a",
                            "parentName": A,
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("invalidRelation", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"INVALID\" is not a valid relation", exception.getMessage());
        tempFile.delete();
    }

    @Test
    void testInvalidCTC() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [
                        {
                            "sourceName": "A",
                            "targetName": "a",
                            "relation": "INVALID"
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
                            "name": "a",
                            "parentName": A,
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;
        var tempFile = File.createTempFile("invalidCTC", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        var reader = new FeatureModelReader(logger);
        var exception = assertThrows(InvalidFeatureModelRelationException.class, () -> reader.readFeatureModelJson(tempFile));
        assertEquals("\"INVALID\" is not a valid cross tree constraint", exception.getMessage());
        tempFile.delete();
    }


}
