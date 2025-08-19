package Filter;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelReader.Structures.Feature;
import Helper.ProgramHelper;
import IO.impl.LshwClass;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FeatureFilter {
    private final Logger _Logger;

    public FeatureFilter(Logger logger) {
        _Logger = logger;
    }

    public List<Feature> filterFeaturesAgainstEdge(List<Feature> features, AvailableEdgeHardware edgeInformation, int maxRequirements) {
        var lshwClassesInOrder = LshwClass.values();
        List<Feature> nonValidFeatures = new ArrayList<>();
        for (var feature : features) {
            var featureRequirements = feature.getHardwareRequirements();
            var edgeRequirements = edgeInformation.edgeHardware;
            for (int i = 0; i < maxRequirements; i++) {
                var currentLshwClass = lshwClassesInOrder[i];
                if (featureRequirements.get(currentLshwClass) != null &&
                        featureRequirements.get(currentLshwClass) > edgeRequirements.get(currentLshwClass)) {
                    nonValidFeatures.add(feature);
                    break;
                }
            }
        }
        return nonValidFeatures;
    }

    /**
     * Filters the features in the given partial configuration based on the available hardware.
     * Unsupported features are removed directly from the configuration.
     *
     * @param configuration the partial configuration to filter (modified in place)
     * @param edgeHardware  the available hardware information
     * @param maxRequirements the maximum number of hardware requirement levels to consider
     */
    public void filterConfigurationByHardware(PartialConfiguration configuration,
                                               AvailableEdgeHardware edgeHardware,
                                               int maxRequirements) {
        // Create a copy of the feature list to avoid concurrent modification issues.
        var featuresCopy = new ArrayList<>(configuration.getFeatures());
        var lshwClasses = LshwClass.values();

        // Iterate over each feature in the copied list.
        for (var feature : featuresCopy) {
            // Get the hardware requirements for the feature.
            var requirements = feature.getHardwareRequirements();
            if (requirements != null) {
                // Check the requirement for each hardware level up to maxRequirements.
                for (var i = 0; i < maxRequirements; i++) {
                    var currentClass = lshwClasses[i];
                    // If the feature has a requirement for the current hardware class,
                    // and the required value exceeds what is available, remove the feature.
                    if (requirements.containsKey(currentClass)) {
                        int required = requirements.get(currentClass);
                        int available = edgeHardware.edgeHardware.getOrDefault(currentClass, 0);
                        if (required > available) {
                            if (ProgramHelper.DEBUG)
                                _Logger.info("Removing feature " + feature.getName() + " - requires " + required +
                                    " but only " + available + " available for " + currentClass);
                            configuration.getFeatures().remove(feature);
                            break; // No need to check further for this feature.
                        }
                    }
                }
            }
        }
    }
}
