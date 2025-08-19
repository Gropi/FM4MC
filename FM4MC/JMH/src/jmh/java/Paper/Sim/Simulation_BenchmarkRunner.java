package Paper.Sim;

import Paper.JMHTestDataProvider;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class Simulation_BenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        var testDataProvider = new JMHTestDataProvider();

        // Set JVM property to use semicolon as CSV delimiter
        System.setProperty("jmh.csv.delimiter", ";");

        var opt = new OptionsBuilder()
                .include(HandcraftedSlicerFMOfflinePhase.class.getSimpleName())
                .param("_FilePathFM", testDataProvider.getTestFilesWithPath())
                // Specify the output file for the results
                .result("slicingSpeedHandcraftedFMs.csv")
                .measurementIterations(30)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
    }
}