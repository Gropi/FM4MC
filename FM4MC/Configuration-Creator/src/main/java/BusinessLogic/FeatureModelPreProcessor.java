package BusinessLogic;

import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelReader.FeatureModelReader;
import FeatureModelSlicer.FeatureModelSlicer;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import IO.IManagingMeasurement;
import IO.impl.ManagingMeasurement;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

/**
 * High level entry point to read, slice, calculate and serialize feature model
 * configurations. It wires together reader, slicer, calculator and serializer
 * components and measures their execution time.
 */
public class FeatureModelPreProcessor {
    private final Logger _ApplicationLogger;
    private IManagingMeasurement _MeasurementLogger;
    private long _StartTime;

    /**
     * Creates a new pre processor with the given logger.
     *
     * @param applicationLogger logger used for progress and error output
     */
    public FeatureModelPreProcessor(Logger applicationLogger) {
        _ApplicationLogger = applicationLogger;
    }

    /**
     * Processes all feature model files within the provided folder.
     *
     * @param folderName folder containing feature model files
     * @param threshold  slicing threshold for estimated configurations
     * @param withSlicing whether the model should be sliced before configuration
     * @param outputPath destination directory for serialized configurations
     */
    public void startTestForFolder(String folderName, int threshold, boolean withSlicing, String outputPath) {
        setupMeasurementLogger();
        _StartTime = System.currentTimeMillis();
        preprocessApplicationFeatureModel(folderName, threshold, withSlicing, outputPath);
    }

    /**
     * Processes a single feature model file.
     *
     * @param fileName   path to the feature model file
     * @param threshold  slicing threshold for estimated configurations
     * @param withSlicing whether the model should be sliced before configuration
     * @param outputPath destination directory for serialized configurations
     */
    public void startTestForFile(String fileName, int threshold, boolean withSlicing, String outputPath) {
        setupMeasurementLogger();
        var file = new File(fileName);
        _StartTime = System.currentTimeMillis();
        preprocessApplicationFeatureModel(file, threshold, withSlicing, outputPath);
    }

    /**
     * Initializes the measurement logger used to record processing times.
     */
    private void setupMeasurementLogger() {
        _MeasurementLogger = new ManagingMeasurement();
        _MeasurementLogger.writeLine("file;reading;slicing;cnfClauseGeneration;confCreation;confSaving");
    }

    /**
     * Iterates over all files in the given folder and processes each as a
     * feature model file.
     */
    private void preprocessApplicationFeatureModel(String folder, int threshold, boolean withSlicing, String outputPath) {
        var fileFolder = new File(folder);
        for (var fileEntry : Objects.requireNonNull(fileFolder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                preprocessApplicationFeatureModel(fileEntry, threshold, withSlicing, outputPath);
            }
        }
    }

    /**
     * Executes the end-to-end preprocessing for a single file: reading,
     * optional slicing, configuration calculation and serialization.
     */
    private void preprocessApplicationFeatureModel(File file, int threshold, boolean withSlicing, String outputPath) {
        var fmReader = new FeatureModelReader(_ApplicationLogger);
        var fmSlicer = new FeatureModelSlicer(_ApplicationLogger);
        var configurationCalculator = new ConfigurationCalculator(_ApplicationLogger);
        var configurationSerializer = new ConfigurationSerializer(_ApplicationLogger);

        try {
            // read json file and create objects for splitting.
            var featureModel = fmReader.readFeatureModelJson(file);
            var readingTime = System.currentTimeMillis() - _StartTime;

            var slicingTime = 0L;
            var cnfClauseGenerationTime = 0L;
            var slicedFeatureModels = new FeatureModelSliced(featureModel);

            FeatureModelPartiallyCalculated partiallyCalculatedFM;
            if (withSlicing) {
                // splits the feature model into smaller feature models.
                slicedFeatureModels = fmSlicer.sliceFeatureModel(featureModel, threshold);
                slicingTime = System.currentTimeMillis() - _StartTime - readingTime;
                partiallyCalculatedFM = configurationCalculator.calculatePartialConfigurations(slicedFeatureModels);
            } else {
                partiallyCalculatedFM = configurationCalculator.calculatedConfigurationForNonSlicedFM(featureModel);
            }

            // calculating the configurations for the partial feature models (can only be one)
            var configurationCreationTime = System.currentTimeMillis() - _StartTime - readingTime - slicingTime - cnfClauseGenerationTime;

            // save configurations to file
            configurationSerializer.saveConfigurations(partiallyCalculatedFM, outputPath);
            var configurationSavingTime = System.currentTimeMillis() - _StartTime - readingTime - slicingTime - cnfClauseGenerationTime - configurationCreationTime;

            _MeasurementLogger.writeLine(file.toString(), readingTime, slicingTime, cnfClauseGenerationTime, configurationCreationTime, configurationSavingTime);
        }  catch (InvalidFeatureModelRelationException e) {
            _ApplicationLogger.fatal(e);
        }
    }
}
