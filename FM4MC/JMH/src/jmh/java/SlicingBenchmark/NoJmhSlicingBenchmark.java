package SlicingBenchmark;

import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.FeatureModelSlicer;
import Helper.LinearFMBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;

public class NoJmhSlicingBenchmark {

    private final Logger _Logger = LogManager.getRootLogger();
    private final String BENCHMARK_RESULT_FILENAME = "benchmark_results.csv";
    private final String CONFIGURATION_FILENAME = "TestData/ConfigurationBenchMarkResults/temporaryConfigurationFile";
    private final String FILE_ENDING = ".csv";
    private final int MIN_TASKS = 2;
    private final int MAX_TASKS = 100;
    private final int MIN_ALTERNATIVES = 1;
    private final int MAX_ALTERNATIVES = 15;
    private final int[] THRESHOLDS = new int[]{10, 25, 50, 75, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};
    private final int TIMEOUT_IN_SECONDS = 7200;

    public void executeBenchmark(boolean useSlicing, boolean useDirectory, String directory) {
        if (useDirectory) {
            if (useSlicing) {
                executeSlicingBenchmarkForFiles(directory);
            } else {
                executeNoSlicingBenchmarkForFiles(directory);
            }
        } else {
            executeLinearBenchmark(useSlicing);
        }
    }

    public void executeSlicingBenchmarkForFiles(String directory) {
        var sb = new StringBuilder();
        sb.append("inputFile;validConfigurations;threshold;fileSizeInByte;timeReadingFM;timeSlicingFM;timeCalculatingConfigurations;timeSavingConfigurations\n");

        var fileFolder = new File(directory);
        for (var fileEntry : Objects.requireNonNull(fileFolder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                var reader = new FeatureModelReader(_Logger);
                FeatureModelRead fmRead;

                var startTimeReading = System.nanoTime();
                try {
                    fmRead = reader.readFeatureModelJson(fileEntry);
                } catch (InvalidFeatureModelRelationException e) {
                    throw new RuntimeException(e);
                }
                var totalTimeReading = System.nanoTime() - startTimeReading;

                for (int threshold : THRESHOLDS) {
                    var slicer = new FeatureModelSlicer(_Logger);
                    var startTimeSlicing = System.nanoTime();
                    var fmSliced = slicer.sliceFeatureModel(fmRead, threshold);
                    var totalTimeSlicing = System.nanoTime() - startTimeSlicing;

                    var configurationCalculator = new ConfigurationCalculator(_Logger);
                    var startTimeConfigurationCalculation = System.nanoTime();
                    var fmPartiallyCalculated = configurationCalculator.calculatePartialConfigurations(fmSliced);
                    var totalTimeConfigurationCalculation = System.nanoTime() - startTimeConfigurationCalculation;

                    var configurationSerializer = new ConfigurationSerializer(_Logger);
                    var startTimeSerializer = System.nanoTime();
                    var filePath = CONFIGURATION_FILENAME + "_" + fileEntry.getName() + "_" + threshold + FILE_ENDING;
                    configurationSerializer.saveConfigurations(fmPartiallyCalculated, filePath);
                    var totalTimeSerializer = System.nanoTime() - startTimeSerializer;
                    var file = new File(filePath);
                    var fileSizeInByte = file.length();
                    _Logger.info("file :" + fileEntry.getName() + " threshold: " + threshold + " fileSize: " + fileSizeInByte);
                    sb.append(fileEntry.getName()).append(";").append(0).append(";").append(threshold).append(";").append(fileSizeInByte).append(";").append(totalTimeReading).append(";").append(totalTimeSlicing).append(";").append(totalTimeConfigurationCalculation).append(";").append(totalTimeSerializer).append("\n");
                    file.delete();
                }
            }
        }

        saveResults(sb);
    }

    private void executeNoSlicingBenchmarkForFiles(String directory) {
        var sb = new StringBuilder();
        sb.append("inputFile;validConfigurations;threshold;fileSizeInByte;timeReadingFM;timeSlicingFM;timeCalculatingConfigurations;timeSavingConfigurations\n");

        var fileFolder = new File(directory);
        for (var fileEntry : Objects.requireNonNull(fileFolder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                var reader = new FeatureModelReader(_Logger);
                FeatureModelRead fmRead = null;
                var startTimeReading = System.nanoTime();
                try {
                    fmRead = reader.readFeatureModelJson(fileEntry);
                } catch (InvalidFeatureModelRelationException e) {
                    throw new RuntimeException(e);
                }
                var totalTimeReading = System.nanoTime() - startTimeReading;

                var totalTimeSlicing = 0;

                var threshold = Integer.MAX_VALUE;
                long totalTimeConfigurationCalculation = -1;
                long totalTimeSerializer = -1;
                long fileSizeInByte = -1;
                var timedOut = false;
                var executor = Executors.newSingleThreadExecutor();
                var startTimeConfigurationCalculation = System.nanoTime();

                Future<FeatureModelPartiallyCalculated> future = executor.submit(new NoSlicingJobSAT(_Logger, fmRead));
                FeatureModelPartiallyCalculated fmPartiallyCalculated = null;

                try {
                    fmPartiallyCalculated = future.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                    totalTimeConfigurationCalculation = System.nanoTime() - startTimeConfigurationCalculation;
                } catch (Exception e) {
                    _Logger.info("Configuration calculation timed out at: " + TIMEOUT_IN_SECONDS + " S");
                    timedOut = true;
                    future.cancel(true);
                } finally {
                    executor.shutdownNow();
                }

                if (!timedOut) {
                    var configurationSerializer = new ConfigurationSerializer(_Logger);
                    var startTimeSerializer = System.nanoTime();

                    var filePath = CONFIGURATION_FILENAME + "_" + fileEntry.getName() + "_" + threshold + FILE_ENDING;
                    configurationSerializer.saveConfigurations(fmPartiallyCalculated, filePath);

                    totalTimeSerializer = System.nanoTime() - startTimeSerializer;


                    var file = new File(filePath);
                    fileSizeInByte = file.length();
                    file.delete();
                }
                _Logger.info("file :" + fileEntry.getName() + " threshold: " + threshold + " fileSize: " + fileSizeInByte);
                sb.append(fileEntry.getName()).append(";")
                        .append(0).append(";").
                        append(threshold).append(";").
                        append(fileSizeInByte).append(";").
                        append(totalTimeReading).append(";").
                        append(totalTimeSlicing).append(";").
                        append(totalTimeConfigurationCalculation).append(";").
                        append(totalTimeSerializer).append("\n");
            }
        }

        saveResults(sb);
    }

    private void executeLinearBenchmark(boolean useSlicing) {
        var sb = new StringBuilder();
        sb.append("amountOfTasks;amountOfAlternativesPerTask;validConfigurations;threshold;fileSizeInByte;timeReadingFM;timeSlicingFM;timeCalculatingConfigurations;timeSavingConfigurations\n");
        var fmBuilder = new LinearFMBuilder();
        var minAlternativeAtTimeout = MAX_ALTERNATIVES + 1;
        for (int t = MIN_TASKS; t <= MAX_TASKS; t++) {
            var timedOut = false;
            for (int a = MIN_ALTERNATIVES; a <= MAX_ALTERNATIVES; a++) {
                var inputFile = fmBuilder.createLinearFM(t, a);

                var reader = new FeatureModelReader(_Logger);
                FeatureModelRead fmRead = null;

                var startTimeReading = System.nanoTime();
                try {
                    fmRead = reader.readFeatureModelJson(inputFile);
                } catch (InvalidFeatureModelRelationException e) {
                    throw new RuntimeException(e);
                }
                var totalTimeReading = System.nanoTime() - startTimeReading;

                if (useSlicing) {
                    for (int threshold : THRESHOLDS) {
                        var slicer = new FeatureModelSlicer(_Logger);
                        var startTimeSlicing = System.nanoTime();
                        var fmSliced = slicer.sliceFeatureModel(fmRead, threshold);
                        var totalTimeSlicing = System.nanoTime() - startTimeSlicing;

                        var configurationCalculator = new ConfigurationCalculator(_Logger);
                        var startTimeConfigurationCalculation = System.nanoTime();

                        var fmPartiallyCalculated = configurationCalculator.calculatePartialConfigurations(fmSliced);

                        var totalTimeConfigurationCalculation = System.nanoTime() - startTimeConfigurationCalculation;


                        var configurationSerializer = new ConfigurationSerializer(_Logger);
                        var startTimeSerializer = System.nanoTime();

                        var filePath = CONFIGURATION_FILENAME + t + "_" + a + "_" + threshold + FILE_ENDING;
                        configurationSerializer.saveConfigurations(fmPartiallyCalculated, filePath);

                        var totalTimeSerializer = System.nanoTime() - startTimeSerializer;


                        var file = new File(filePath);
                        var fileSizeInByte = file.length();
                        file.delete();

                        _Logger.info("tasks: " + t + " alternatives: " + a + " threshold: " + threshold + " fileSize: " + fileSizeInByte);
                        sb.append(t).append(";")
                                .append(a).append(";")
                                .append(0).append(";")
                                .append(threshold).append(";")
                                .append(fileSizeInByte).append(";")
                                .append(totalTimeReading / 1_000).append(";")
                                .append(totalTimeSlicing / 1_000).append(";")
                                .append(totalTimeConfigurationCalculation / 1000).append(";")
                                .append(totalTimeSerializer / 1_000).append("\n");
                    }
                } else {
                    var threshold = Integer.MAX_VALUE;

                    var totalTimeSlicing = 0;

                    long totalTimeConfigurationCalculation = -1;
                    long totalTimeSerializer = -1;
                    long fileSizeInByte = -1;
                    if (!timedOut && a < minAlternativeAtTimeout) {
                        var executor = Executors.newSingleThreadExecutor();
                        var startTimeConfigurationCalculation = System.nanoTime();

                        Future<FeatureModelPartiallyCalculated> future = executor.submit(new NoSlicingJobSAT(_Logger, fmRead));
                        FeatureModelPartiallyCalculated fmPartiallyCalculated = null;

                        try {
                            fmPartiallyCalculated = future.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                            totalTimeConfigurationCalculation = System.nanoTime() - startTimeConfigurationCalculation;
                        } catch (Exception e) {
                            _Logger.info("Configuration calculation timed out at: " + TIMEOUT_IN_SECONDS + " S");
                            future.cancel(true);
                            timedOut = true;
                            minAlternativeAtTimeout = a;
                        } finally {
                            executor.shutdownNow();
                        }

                        if (!timedOut) {
                            var configurationSerializer = new ConfigurationSerializer(_Logger);
                            var startTimeSerializer = System.nanoTime();

                            var filePath = CONFIGURATION_FILENAME + t + "_" + a + "_NoSlicing" + FILE_ENDING;
                            configurationSerializer.saveConfigurations(fmPartiallyCalculated, filePath);

                            totalTimeSerializer = System.nanoTime() - startTimeSerializer;


                            var file = new File(filePath);
                            fileSizeInByte = file.length();
                            file.delete();
                        }
                    }

                    _Logger.info("tasks: " + t + " alternatives: " + a + " no slicing fileSize: " + fileSizeInByte);
                    sb.append(t).append(";")
                            .append(a).append(";")
                            .append(0).append(";")
                            .append(threshold).append(";")
                            .append(fileSizeInByte).append(";")
                            .append(totalTimeReading / 1_000).append(";")
                            .append(totalTimeSlicing / 1_000).append(";")
                            .append(totalTimeConfigurationCalculation < 0 ? totalTimeConfigurationCalculation : (totalTimeConfigurationCalculation / 1000)).append(";")
                            .append(totalTimeSerializer < 0 ? totalTimeSerializer : (totalTimeSerializer / 1000)).append("\n");
                }
            }
        }

        saveResults(sb);
    }

    private void saveResults(StringBuilder sb) {
        try {
            var br = new BufferedWriter(new FileWriter(BENCHMARK_RESULT_FILENAME));
            br.write(sb.toString());
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
