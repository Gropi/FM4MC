package Paper.Offline;

import Paper.JMHTestDataProvider;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class OfflineBenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        var testDataProvider = new JMHTestDataProvider();
        // Set JVM property to use semicolon as CSV delimiter
        System.setProperty("jmh.csv.delimiter", ";");

        var opt1 = new OptionsBuilder()
                .include(ConfigurationCalculatorBenchmark.class.getSimpleName())
                .param("_FilePathFM", testDataProvider.getTestFilesWithPath())
                .param("_Thresholds", testDataProvider.getThresholds())
                // Specify the output file for the results
                .result("configurationCalculator.csv")
                .measurementIterations(30)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        var opt2 = new OptionsBuilder()
                .include(SlicerBenchmark.class.getSimpleName())
                .param("_FilePathFM", testDataProvider.getTestFilesWithPath())
                .param("_Thresholds", testDataProvider.getThresholds())
                // Specify the output file for the results
                .result("slicerBenchmark.csv")
                .measurementIterations(30)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        var opt3 = new OptionsBuilder()
                .include(ConfigurationCalculatorBenchmark_NoSlicing.class.getSimpleName())
                .param("_FilePathFM", testDataProvider.getTestFilesWithPath())
                .timeout(TimeValue.hours(1))
                // Specify the output file for the results
                .result("configurationCalculator_NoSlicing.csv")
                .measurementIterations(3)
                .warmupIterations(3)
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt1).run();
        new Runner(opt2).run();
        new Runner(opt3).run();
    }
}