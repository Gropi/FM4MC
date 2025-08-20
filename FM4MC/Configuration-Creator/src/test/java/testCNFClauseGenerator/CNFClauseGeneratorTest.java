package testCNFClauseGenerator;

import CNFClauseGenerator.CNFClauseGenerator;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.Structures.Feature;
import FeatureModelSlicer.FeatureModelSlicer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CNFClauseGeneratorTest {
    private static final Logger logger = LogManager.getLogger(CNFClauseGeneratorTest.class);

    @Test
    public void testAbstractLayerClauses() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForAbstractLayerTesting.json");
        var reader = new FeatureModelReader(logger);
        var slicer = new FeatureModelSlicer(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var model = reader.readFeatureModelJson(readFile);
        var clauses = clauseGenerator.createAbstractLayerClauses(slicer.sliceFeatureModel(model, 0));
        int sumClauses = 1 + 1 + 2 + 2 + 1 + 2 + 2 + 2 + 3 + 11;
        assertEquals(sumClauses, clauses.size(), "There should be 38 clauses: header, root, 2 for start, 2 for mandatory, 1 for optional, 2 for feature group OR, 2 for feature group XOR, 2 for end, 3 for the OR Features, 11 for the XOR features");
        assertEquals(clauses.getFirst()[0], clauses.subList(1, clauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
        assertEquals(clauses.getFirst()[1], clauses.size() - 1, "the second parameter of the header is number of clauses");
        assertEquals(5, clauses.stream().map(clause -> clause.length).max(Comparator.naturalOrder()).get(), "the largest clause should contain 5 literals");
        var concreteIndices = model.features.stream().filter(f -> f.getChildren().isEmpty()).map(Feature::getIndex).collect(Collectors.toSet());
        assertTrue(clauses.stream().allMatch(clause -> Arrays.stream(clause).noneMatch(literal -> concreteIndices.contains(Math.abs(literal)))), "No Clause should contain concrete features");
    }

    @Test
    public void testConcreteLayerClauses() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForAbstractLayerTesting.json");
        var reader = new FeatureModelReader(logger);
        var slicer = new FeatureModelSlicer(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var model = reader.readFeatureModelJson(readFile);
        var slicedModel = slicer.sliceFeatureModel(model, 0);

        slicedModel.partialConcreteFeatureModels.forEach(pfm -> {
            var parents = pfm.stream().filter(feature -> !feature.getChildren().isEmpty()).toList();
            assertFalse(parents.isEmpty(), "There should be at least one parent feature");
            var clauses = clauseGenerator.createConcretePFMClauses(parents, slicedModel.crossTreeConstraints);
            assertEquals(clauses.get(0)[0], clauses.subList(1, clauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
            assertEquals(clauses.get(0)[1], clauses.size() - 1, "the second parameter of the header is number of clauses");
            var clausesSet = clauses.subList(1, clauses.size()).stream().map(clause -> Arrays.stream(clause).boxed().collect(Collectors.toSet())).collect(Collectors.toSet());
            parents.forEach(parent -> {
                assertTrue(clausesSet.contains(Set.of(parent.getIndex())), "The clauses should contain all parent indices");
            });
            if (parents.stream().allMatch(parent -> parent.getChildren().size() == 1)) {
                assertEquals(1 + 3 * parents.size(), clauses.size(), "there should be 3 clauses per parent if they all have only 1 child");
            } else {
                int maxChildren = parents.stream().map(parent -> parent.getChildren().size()).max(Comparator.naturalOrder()).get();
                //header + parent clause + pair of children clauses + clause for each feature
                assertTrue(1 + parents.size() + (((maxChildren * (maxChildren - 1)) / 2) * parents.size()) + ((maxChildren + 1) * parents.size()) >= clauses.size());
            }
        });
    }

    @Test
    public void testNonSliceFMClauseGeneration() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForAbstractLayerTesting.json");
        var reader = new FeatureModelReader(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var model = reader.readFeatureModelJson(readFile);
        var clauses = clauseGenerator.createClausesForNonSlicedFM(model);
        assertEquals(47, clauses.size());
        assertEquals(clauses.get(0)[0], clauses.subList(1, clauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
        assertEquals(clauses.get(0)[1], clauses.size() - 1, "the second parameter of the header is number of clauses");
        assertArrayEquals(new int[]{1}, clauses.get(1));
        model.features.forEach(feature -> {
            assertTrue(clauses.stream().anyMatch(clause -> Arrays.stream(clause).anyMatch(literal -> Math.abs(literal) == feature.getIndex())));
        });
    }

    @Test
    public void testClauseGenerationWithCTCs() throws Exception {
        var readFile = new File("../Configuration-Creator/src/test/resources/FMForLocalRequireCTCTest.json");

        var reader = new FeatureModelReader(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var slicer = new FeatureModelSlicer(logger);
        var model = reader.readFeatureModelJson(readFile);

        // Assuming that the FeatureModelRead class contains a list "crossTreeConstraints"
        var constraints = model.crossTreeConstraints;
        assertNotNull(constraints, "Constraints list should not be null");
        assertEquals(2, constraints.size(), "There should be 2 cross-tree constraints");
        var clauses = clauseGenerator.createClausesForNonSlicedFM(model);
        assertNotNull(constraints, "Constraints list should not be null");
        assertTrue(constraints.isEmpty(), "There should be no clauses after calculating configurations");
        assertEquals(clauses.get(0)[0], clauses.subList(1, clauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
        assertEquals(clauses.get(0)[1], clauses.size() - 1, "the second parameter of the header is number of clauses");
        assertArrayEquals(new int[]{1}, clauses.get(1));
        // a1 index = 6, b1 index = 8, a2 index = 7, b2 index = 9
        assertTrue(clauses.stream().anyMatch(clause -> clause.length == 2 && Arrays.stream(clause).allMatch(literal -> literal == -6 || literal == 8)), "clauses should contain {-6, 8} (a1 requires b1)");
        assertTrue(clauses.stream().anyMatch(clause -> clause.length == 2 && Arrays.stream(clause).allMatch(literal -> literal == -7 || literal == 9)), "clauses should contain {-7, 9} (a2 requires b2)");

        model = reader.readFeatureModelJson(readFile);
        var slicedModel = slicer.sliceFeatureModel(model, 2);
        constraints = slicedModel.crossTreeConstraints;
        assertNotNull(constraints, "Constraints list should not be null");
        assertEquals(2, constraints.size(), "There should be 2 cross-tree constraints");
        var pfmClauses = new ArrayList<List<int[]>>();
        slicedModel.partialConcreteFeatureModels.forEach(pfm -> {
            var parents = pfm.stream().filter(f -> f.getChildren().stream().anyMatch(child -> child.getChildren().isEmpty())).toList();
            pfmClauses.add(clauseGenerator.createConcretePFMClauses(parents, slicedModel.crossTreeConstraints));
        });
        assertEquals(2, pfmClauses.size(), "There should be clauses for 2 pfms");
        assertEquals(2, constraints.size(), "There should be 2 cross-tree constraints left when no constraint is local");

        model = reader.readFeatureModelJson(readFile);
        var slicedModel2 = slicer.sliceFeatureModel(model, 4);
        constraints = slicedModel2.crossTreeConstraints;
        assertNotNull(constraints, "Constraints list should not be null");
        assertEquals(2, constraints.size(), "There should be 2 cross-tree constraints");
        var pfmClauses2 = new ArrayList<List<int[]>>();
        slicedModel2.partialConcreteFeatureModels.forEach(pfm -> {
            var parents = pfm.stream().filter(f -> f.getChildren().stream().anyMatch(child -> child.getChildren().isEmpty())).toList();
            pfmClauses2.add(clauseGenerator.createConcretePFMClauses(parents, slicedModel2.crossTreeConstraints));
        });
        assertEquals(1, pfmClauses2.size(), "There should be clauses for 1 pfms");
        assertEquals(0, constraints.size(), "There should be no cross-tree constraints left when all constraints are local");
        assertTrue(pfmClauses2.get(0).stream().anyMatch(clause -> clause.length == 2 && Arrays.stream(clause).allMatch(literal -> literal == -6 || literal == 8)), "clauses should contain {-6, 8} (a1 requires b1)");
        assertTrue(pfmClauses2.get(0).stream().anyMatch(clause -> clause.length == 2 && Arrays.stream(clause).allMatch(literal -> literal == -7 || literal == 9)), "clauses should contain {-7, 9} (a2 requires b2)");

    }


    @Test
    public void testClauseGenerationForMandatoryFeatures() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "A",
                                "endTask"
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
                        },
                        {
                            "name": "A",
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
                        }
                    ]
                }
                """;

        // Write JSON to temporary file
        var tempFile = File.createTempFile("testMandatoryClause", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var slicer = new FeatureModelSlicer(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var model = reader.readFeatureModelJson(tempFile);
        var slicedModel = slicer.sliceFeatureModel(model, 4);
        var abstractClauses = clauseGenerator.createAbstractLayerClauses(slicedModel);

        assertEquals(8, abstractClauses.size(), "There should be 8 clauses: header, root, 2 for start, 2 for mandatory, 2 for end,");
        assertEquals(abstractClauses.get(0)[0], abstractClauses.subList(1, abstractClauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
        assertEquals(abstractClauses.get(0)[1], abstractClauses.size() - 1, "the second parameter of the header is number of clauses");
        List<int[]> expected = List.of(new int[]{1}, new int[]{2, -1}, new int[]{-2, 1}, new int[]{4, -1}, new int[]{-4, 1}, new int[]{6, -1}, new int[]{-6, 1});
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), abstractClauses.get(i + 1));
        }
        tempFile.delete();
    }

    @Test
    public void testClauseGenerationForOptionalFeatures() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "A",
                                "endTask"
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
                            "hardwareRequirements": [],
                            "name": "a",
                            "parentName": "A",
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;

        // Write JSON to temporary file
        var tempFile = File.createTempFile("testOptionalClause", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var slicer = new FeatureModelSlicer(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var model = reader.readFeatureModelJson(tempFile);
        var slicedModel = slicer.sliceFeatureModel(model, 4);
        var abstractClauses = clauseGenerator.createAbstractLayerClauses(slicedModel);

        assertEquals(7, abstractClauses.size(), "There should be 7 clauses: header, root, 2 for start, 1 for optional, 2 for end,");
        assertEquals(abstractClauses.get(0)[0], abstractClauses.subList(1, abstractClauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
        assertEquals(abstractClauses.get(0)[1], abstractClauses.size() - 1, "the second parameter of the header is number of clauses");
        List<int[]> expected = List.of(new int[]{1}, new int[]{2, -1}, new int[]{-2, 1}, new int[]{4, -1}, new int[]{-4, 1}, new int[]{-6, 1});
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), abstractClauses.get(i + 1));
        }
        tempFile.delete();
    }

    @Test
    public void testClauseGenerationForXORFeatureGroups() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "A",
                                "B",
                                "C",
                                "endTask"
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
                        },
                        {
                            "name": "XORGroup",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "A",
                            "parentName": "XORGroup",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "a",
                            "parentName": "A",
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "B",
                            "parentName": "XORGroup",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "b",
                            "parentName": "B",
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "C",
                            "parentName": "XORGroup",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "ALTERNATIVE"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "c",
                            "parentName": "C",
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;

        // Write JSON to temporary file
        var tempFile = File.createTempFile("testXORClause", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var slicer = new FeatureModelSlicer(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var model = reader.readFeatureModelJson(tempFile);
        var slicedModel = slicer.sliceFeatureModel(model, 4);
        var abstractClauses = clauseGenerator.createAbstractLayerClauses(slicedModel);

        assertEquals(15, abstractClauses.size(), "There should be 15 clauses: header, root, 2 for start, 2 for end, 2 for the group parent, 7 for the xor group children");
        assertEquals(abstractClauses.get(0)[0], abstractClauses.subList(1, abstractClauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
        assertEquals(abstractClauses.get(0)[1], abstractClauses.size() - 1, "the second parameter of the header is number of clauses");
        List<int[]> expected = List.of(new int[]{1}, new int[]{2, -1}, new int[]{-2, 1}, new int[]{4, -1}, new int[]{-4, 1}, new int[]{6, -1}, new int[]{-6, 1});
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), abstractClauses.get(i + 1));
        }
        //parent index 6, childrenIndices: 7, 9, 11
        //1 clause for each pair of children + 1 clause for each feature (including parent)
        Set<Set<Integer>> expectedXORClauses = Set.of(Set.of(-7, -9), Set.of(-7, -11), Set.of(-9, -11), Set.of(-6, 7, 9, 11), Set.of(6, -7, 9, 11), Set.of(6, 7, -9, 11), Set.of(6, 7, 9, -11));
        var xorClauses = abstractClauses.subList(expected.size() + 1, abstractClauses.size()).stream().map(clause -> Arrays.stream(clause).boxed().collect(Collectors.toSet())).collect(Collectors.toSet());
        assertEquals(expectedXORClauses.size(), xorClauses.size());
        assertEquals(xorClauses, expectedXORClauses);
        tempFile.delete();
    }

    @Test
    public void testClauseGenerationForORFeatureGroups() throws Exception {
        var json = """
                {
                    "crossTreeConstraints": [],
                    "features": [
                        {
                            "name": "startTask",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "A",
                                "B",
                                "C",
                                "endTask"
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
                        },
                        {
                            "name": "ORGroup",
                            "parentName": "root",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "A",
                            "parentName": "ORGroup",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "OR"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "a",
                            "parentName": "A",
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "B",
                            "parentName": "ORGroup",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "OR"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "b",
                            "parentName": "B",
                            "relation": "MANDATORY"
                        },
                        {
                            "name": "C",
                            "parentName": "ORGroup",
                            "reachableAbstractFeatures": [
                                "endTask"
                            ],
                            "relation": "OR"
                        },
                        {
                            "hardwareRequirements": [],
                            "name": "c",
                            "parentName": "C",
                            "relation": "MANDATORY"
                        }
                    ]
                }
                """;

        // Write JSON to temporary file
        var tempFile = File.createTempFile("testORClause", ".json");
        try (var writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        var reader = new FeatureModelReader(logger);
        var slicer = new FeatureModelSlicer(logger);
        var clauseGenerator = new CNFClauseGenerator(logger);
        var model = reader.readFeatureModelJson(tempFile);
        var slicedModel = slicer.sliceFeatureModel(model, 4);
        var abstractClauses = clauseGenerator.createAbstractLayerClauses(slicedModel);

        assertEquals(12, abstractClauses.size(), "There should be 12 clauses: header, root, 2 for start, 2 for end, 2 for the group parent, 3 for the or group children + 1 for the combination");
        assertEquals(abstractClauses.get(0)[0], abstractClauses.subList(1, abstractClauses.size()).stream().map(clause -> Arrays.stream(clause).map(Math::abs).max().getAsInt()).max(Comparator.naturalOrder()).get(), "the first parameter of the header is the highest occurring literal");
        assertEquals(abstractClauses.get(0)[1], abstractClauses.size() - 1, "the second parameter of the header is number of clauses");
        List<int[]> expected = List.of(new int[]{1}, new int[]{2, -1}, new int[]{-2, 1}, new int[]{4, -1}, new int[]{-4, 1}, new int[]{6, -1}, new int[]{-6, 1});
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), abstractClauses.get(i + 1));
        }
        //parent index 6, childrenIndices: 7, 9, 11
        //1 clause for each child and 1 clause that at least 1 child is selected
        Set<Set<Integer>> expectedORClauses = Set.of(Set.of(6, -7), Set.of(6, -9), Set.of(6, -11), Set.of(-6, 7, 9, 11));
        var orClauses = abstractClauses.subList(expected.size() + 1, abstractClauses.size()).stream().map(clause -> Arrays.stream(clause).boxed().collect(Collectors.toSet())).collect(Collectors.toSet());
        assertEquals(expectedORClauses.size(), orClauses.size());
        assertEquals(orClauses, expectedORClauses);
        tempFile.delete();
    }
}
