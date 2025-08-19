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

public class FeatureModelPreProcessor {
    private final Logger _ApplicationLogger;
    private IManagingMeasurement _MeasurementLogger;
    private long _StartTime;

    public FeatureModelPreProcessor(Logger applicationLogger) {
        _ApplicationLogger = applicationLogger;
    }

    public void startTestForFolder(String folderName, int threshold, boolean withSlicing, String outputPath) {
        setupMeasurementLogger();
        _StartTime = System.currentTimeMillis();
        preprocessApplicationFeatureModel(folderName, threshold, withSlicing, outputPath);
    }

    public void startTestForFile(String fileName, int threshold, boolean withSlicing, String outputPath) {
        setupMeasurementLogger();
        var file = new File(fileName);
        _StartTime = System.currentTimeMillis();
        preprocessApplicationFeatureModel(file, threshold, withSlicing, outputPath);
    }

    private void setupMeasurementLogger() {
        _MeasurementLogger = new ManagingMeasurement();
        _MeasurementLogger.writeLine("file;reading;slicing;cnfClauseGeneration;confCreation;confSaving");
    }

    private void preprocessApplicationFeatureModel(String folder, int threshold, boolean withSlicing, String outputPath) {
        var fileFolder = new File(folder);
        for (var fileEntry : Objects.requireNonNull(fileFolder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                preprocessApplicationFeatureModel(fileEntry, threshold, withSlicing, outputPath);
            }
        }
    }

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
