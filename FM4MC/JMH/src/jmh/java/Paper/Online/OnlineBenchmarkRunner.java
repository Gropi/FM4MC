package Paper.Online;

import Paper.JMHTestDataProvider;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class OnlineBenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        var testDataProvider = new JMHTestDataProvider();

        // Set JVM property to use semicolon as CSV delimiter
        System.setProperty("jmh.csv.delimiter", ";");

        var opt = new OptionsBuilder()
                .include(OnlinePhaseForHandcraftedFMs.class.getSimpleName() + ".onlinePhaseBenchmark")
                .param("_FilePathFM", testDataProvider.getTestFilesWithPath(new int[]{0, 1, 2, 3}))
                .param("_MaxRequirements", testDataProvider.getMaxRequirements())
                .param("_EdgeIndex", testDataProvider.getEdgeIDs())
                // Specify the output file for the results
                .result("onlineBenchmark.csv")
                .measurementIterations(20)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        var opt1 = new OptionsBuilder()
                .include(OnlinePhaseForHandcraftedFMs.class.getSimpleName() + ".onlinePhaseBenchmarkLessIterations")
                .param("_FilePathFM", testDataProvider.getTestFilesWithPath(new int[]{4}))
                .param("_MaxRequirements", testDataProvider.getMaxRequirementsInverted())
                .param("_EdgeIndex", testDataProvider.getEdgeIDsInverted())
                // Specify the output file for the results
                .result("onlineBenchmarkHuge.csv")
                .measurementIterations(5)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        var opt3 = new OptionsBuilder()
                .include(OnlySATSolver.class.getSimpleName())
                .param("_FilePathFM", testDataProvider.getTestFilesWithPath(new int[]{0, 1, 2}))
                // Specify the output file for the results
                .result("onlySATSolver.csv")
                .measurementIterations(20)
                .warmupIterations(3)
                .timeout(TimeValue.hours(2))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
        new Runner(opt1).run();
    }
}