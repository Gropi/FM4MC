package ConfigurationSerializer;

import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.CrossTreeConstraint;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelReader.Structures.CrossTreeConstraintRelation;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import IO.impl.DriveHandle;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes and deserializes configuration results of the configuration
 * calculation. The serializer stores configurations and cross tree
 * constraints in a simple CSV based representation and can restore the
 * {@link FeatureModelPartiallyCalculated} structure from such files.
 */
public class ConfigurationSerializer {

    private final String PARENTS = "parents";
    private final String ABSTRACT_CONFIGURATION = "abstract";
    private final String CONFIGURATION = "conf";
    private final String CROSS_TREE_CONSTRAINT = "CTC";
    private final String LINE_BREAK = "\n";
    private final Logger _ApplicationLogger;
    private final String _DELIMITER = ";";

    /**
     * Creates a new serializer instance.
     *
     * @param logger application logger used for error reporting
     */
    public ConfigurationSerializer(Logger logger) {
        _ApplicationLogger = logger;
    }

    /**
     * saves all calculated configurations of a (partial) feature model to a csv file
     * Format:
     * 1. CTCs
     * 2. Configurations (PFM)
     * 3. Configurations of the abstract Layer
     *
     * @param featureModelConfigurations FM with calculated configurations
     * @param filePath path to csv file
     */
    public void saveConfigurations(FeatureModelPartiallyCalculated featureModelConfigurations, String filePath) {
        var stringBuilder = new StringBuilder();
        saveCTCs(featureModelConfigurations.crossTreeConstraints, stringBuilder);
        saveConcretePartialConfigurations(featureModelConfigurations.configurationsPerPartialFeatureModel, stringBuilder);
        saveAbstractConfigurations(featureModelConfigurations.abstractConfigurations, stringBuilder);
        saveToFile(filePath, stringBuilder);
    }

    /**
     * Appends the abstract configurations to the given {@link StringBuilder}.
     * Each configuration is written on a separate line prefixed with
     * {@code ABSTRACT_CONFIGURATION} and an id.
     *
     * @param abstractConfigurations configurations of the abstract layer
     * @param stringBuilder          builder receiving the serialized output
     */
    private void saveAbstractConfigurations(List<List<Feature>> abstractConfigurations, StringBuilder stringBuilder) {
        int id = 1;
        for (var abstractConfiguration : abstractConfigurations) {
            stringBuilder.append(ABSTRACT_CONFIGURATION)
                    .append(_DELIMITER).append(id);
            for (var abstractFeature : abstractConfiguration) {
                stringBuilder.append(_DELIMITER)
                        .append(abstractFeature.getName());
            }
            stringBuilder.append(LINE_BREAK);
            id++;
        }
    }

    /**
     * Serializes all partial configurations for each partial feature model.
     *
     * @param configurationsPerPartialFeatureModel configurations per PFM
     * @param stringBuilder                        builder receiving the serialized output
     */
    private void saveConcretePartialConfigurations(List<List<PartialConfiguration>> configurationsPerPartialFeatureModel, StringBuilder stringBuilder) {
        int id = 1;
        for (var partialConfigurations : configurationsPerPartialFeatureModel) {
            for (var configuration : partialConfigurations) {
                stringBuilder.append(CONFIGURATION)
                        .append(_DELIMITER).append(id);
                for (var feature : configuration.getFeatures()) {

                    stringBuilder.append(_DELIMITER)
                            .append(feature.getName());
                }
                stringBuilder.append(_DELIMITER).append(PARENTS);
                for (var parent : configuration.getAbstractParent()) {
                    stringBuilder.append(_DELIMITER)
                            .append(parent.getName());
                }
                stringBuilder.append(LINE_BREAK);
            }
            id++;
        }
    }

    /**
     * Writes cross tree constraints to the {@link StringBuilder} in a simple
     * textual format.
     *
     * @param crossTreeConstraints list of constraints to serialize
     * @param stringBuilder        builder receiving the serialized output
     */
    private void saveCTCs(List<CrossTreeConstraint> crossTreeConstraints, StringBuilder stringBuilder) {
        for (var constraint : crossTreeConstraints) {
            String relationString = null;
            switch (constraint.getRelation()) {
                case REQUIRES -> relationString = "requires";
                case EXCLUDES -> relationString = "excludes";
            }

            stringBuilder.append(CROSS_TREE_CONSTRAINT).append(_DELIMITER)
                    .append(relationString).append(_DELIMITER)
                    .append(constraint.getSource().getName()).append(_DELIMITER)
                    .append(constraint.getTarget().getName())
                    .append(LINE_BREAK);
        }
    }

    /**
     * loads precalculated configurations for the given feature model
     * will lead to NullPointerExceptions when the loaded FM does not match the loaded configurations
     *
     * @param featureModelRead feature model
     * @param configurationFilePath path to a csv file containing configurations for the given fm
     * @return a (sliced) feature model with calculated configurations for abstract and concrete layer
     */
    public FeatureModelPartiallyCalculated loadConfigurations(FeatureModelRead featureModelRead, String configurationFilePath) {
        var featureModelTmp = new FeatureModelSliced(featureModelRead);
        var featureModel = new FeatureModelPartiallyCalculated(featureModelTmp);

        try {
            var bufferedReader = new BufferedReader(new FileReader(configurationFilePath));
            String line;
            var configurations = new ArrayList<PartialConfiguration>();
            var abstractConfiguration = new ArrayList<Feature>();
            var currentConfigurationId = 1;
            var currentAbstractConfigurationId = 1;
            while ((line = bufferedReader.readLine()) != null) {
                var splitLine = line.split(_DELIMITER);
                var type = splitLine[0];
                switch (type) {
                    case CROSS_TREE_CONSTRAINT -> {
                        var relationString = splitLine[1];
                        CrossTreeConstraintRelation relation = null;
                        switch (relationString) {
                            case "requires" -> relation = CrossTreeConstraintRelation.REQUIRES;
                            case "excludes" -> relation = CrossTreeConstraintRelation.EXCLUDES;
                        }

                        var sourceFeature = getFeatureByName(featureModel.features, splitLine[2]);
                        var targetFeature = getFeatureByName(featureModel.features, splitLine[3]);
                        var crossTreeConstraint = new CrossTreeConstraint(sourceFeature, targetFeature, relation);
                        featureModel.crossTreeConstraints.add(crossTreeConstraint);
                    }
                    case CONFIGURATION -> {
                        var id = Integer.parseInt(splitLine[1]);
                        if (id > currentConfigurationId) {
                            featureModel.configurationsPerPartialFeatureModel.add(configurations);
                            currentConfigurationId = id;
                            configurations = new ArrayList<>();
                        }
                        var configuration = new PartialConfiguration();
                        int i = 2;
                        for (; i < splitLine.length && !splitLine[i].equals(PARENTS); i++) {
                            configuration.addFeature(getFeatureByName(featureModel.features, splitLine[i]));
                        }
                        i++;
                        for (; i < splitLine.length; i++) {
                            configuration.addAbstractParent(getFeatureByName(featureModel.features, splitLine[i]));

                        }
                        configurations.add(configuration);
                    }
                    case ABSTRACT_CONFIGURATION -> {
                        var id = Integer.parseInt(splitLine[1]);
                        if (id > currentAbstractConfigurationId) {
                            featureModel.abstractConfigurations.add(abstractConfiguration);
                            currentAbstractConfigurationId = id;
                            abstractConfiguration = new ArrayList<>();
                        }
                        for (int i = 2; i < splitLine.length; i++) {
                            abstractConfiguration.add(getFeatureByName(featureModel.features, splitLine[i]));
                        }
                    }
                }
            }
            featureModel.configurationsPerPartialFeatureModel.add(configurations);
            featureModel.abstractConfigurations.add(abstractConfiguration);

        } catch (IOException e) {
            _ApplicationLogger.fatal(e);
        }

        return featureModel;
    }

    /**
     * Finds a feature by name in the provided list.
     *
     * @param features    list of available features
     * @param featureName name to look up
     * @return matching feature or {@code null} if none exists
     */
    private Feature getFeatureByName(List<Feature> features, String featureName) {
        return features.stream().filter(x -> x.getName().equals(featureName)).findFirst().orElse(null);
    }

    /**
     * Writes the accumulated serialization to the specified file.
     *
     * @param fileToWriteTo destination file path
     * @param stringBuilder serialized contents
     */
    private void saveToFile(String fileToWriteTo, StringBuilder stringBuilder) {
        try {
            new DriveHandle().createFolderFromFile(fileToWriteTo);
            var writer = new BufferedWriter(new FileWriter(fileToWriteTo));
            writer.write(stringBuilder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
