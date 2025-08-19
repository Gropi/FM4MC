package Helper;

import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import FeatureModelReader.Structures.Feature;
import IO.impl.EdgeHardwareInformation;
import IO.impl.EdgeNodeHardwareInformation;
import IO.impl.LshwClass;

import java.util.List;
import java.util.Random;

public class RequirementRandomizer {

    private double _Mean = 1000;
    private double _StandardDeviation = 200;

    public RequirementRandomizer() {

    }

    public RequirementRandomizer(double mean, double standardDeviation) {
        _Mean = mean;
        _StandardDeviation = standardDeviation;
    }

    public EdgeHardwareInformation randomizeEdge(int amountOfEdgeNodes, double standardDeviationMultiplier) {
        var edgeHardware = new EdgeHardwareInformation();

        for (var i = 0; i < amountOfEdgeNodes; i++) {
            var nodeHardware = new EdgeNodeHardwareInformation();
            for (var lshwClass : LshwClass.values()) {
                var randomizedValue = generateRandomizedValue(standardDeviationMultiplier);
                nodeHardware.hardwareInformationMap.put(lshwClass, randomizedValue);
            }
            edgeHardware.hardwareInformation.add(nodeHardware);
        }
        return edgeHardware;
    }

    public void randomizeFeatureRequirements(List<Feature> features, double standardDeviationMultiplier) {
        for (var feature : features) {
            for (var lshwClass : LshwClass.values()) {
                var randomizedValue = generateRandomizedValue(standardDeviationMultiplier);
                feature.getHardwareRequirements().put(lshwClass, randomizedValue);
            }
        }
    }

    private int generateRandomizedValue(double multiplier) {
        var generator = new Random();
        return (int) generator.nextGaussian(_Mean, (_StandardDeviation * multiplier));
    }
}
