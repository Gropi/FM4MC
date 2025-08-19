import CNFClauseGenerator.CNFClauseGenerator;
import ConfigurationCalculator.ConfigurationCalculator;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import edgeNodeReader.EdgeNodeReader;
import edgeNodeReader.structures.EdgeNode;
import modules.AVA;
import modules.EDAF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Startup {

    private static final Logger _Logger = LogManager.getLogger("executionLog");


    public static void main(String[] args) {
        var arguments = getParameters(args);
        EdgeNodeReader edgeNodeReader = new EdgeNodeReader(_Logger);
        FeatureModelReader featureModelReader = new FeatureModelReader(_Logger);
        CNFClauseGenerator clauseGenerator = new CNFClauseGenerator(_Logger);
        ConfigurationCalculator calculator = new ConfigurationCalculator(_Logger);
        int maxRequirements = 14;

        if (!arguments.containsKey("edge")) {
            System.out.println("No Edge Config");
            return;
        }
        if (!arguments.containsKey("fm")) {
            System.out.println("No FM file");
            return;
        }
        List<EdgeNode> edgeNodes = edgeNodeReader.readEdgeNodeJson(new File(arguments.get("edge")));
        FeatureModelRead fm;
        try {
            fm = featureModelReader.readFeatureModelJson(new File(arguments.get("fm")));
        } catch (InvalidFeatureModelRelationException e) {
            System.out.println("Invalid Feature Model");
            return;
        }

        AVA ava = new AVA();
        EdgeNode[] edgeNodesArray = new EdgeNode[edgeNodes.size()];
        edgeNodes.toArray(edgeNodesArray);
        var invalidFeatures = ava.adaptApplication(edgeNodesArray, fm, maxRequirements);

        for (Feature feature : invalidFeatures) {
            fm.features.remove(feature);
        }
        var calculatedFM = calculator.calculatedConfigurationForNonSlicedFM(fm);

        EDAF edaf = new EDAF();

        calculatedFM.configurationsPerPartialFeatureModel.get(0).forEach(configuration -> {
            edaf.calculateTaskDeployment(edgeNodesArray, configuration, calculatedFM, true, maxRequirements).forEach(map -> {
                        map.keySet().forEach(key -> {
                            System.out.println(key.getName() + " -> " + map.get(key).id);
                        });
                        System.out.println();
                    }
            );
        });


    }

    private static HashMap<String, String> getParameters(String[] args) {
        HashMap<String, String> parameters = new HashMap<>();

        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equalsIgnoreCase("-edge")) {
                parameters.put("edge", args[i + 1]);
            } else if (args[i].equalsIgnoreCase("-fm")) {
                parameters.put("fm", args[i + 1]);
            } else if (args[i].equalsIgnoreCase("-out")) {
                parameters.put("out", args[i + 1]);
            }
        }

        return parameters;
    }
}
