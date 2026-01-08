package Artifacts;

import Paper.Offline.ConfigurationCalculatorBenchmark;
import Paper.Offline.ConfigurationCalculatorBenchmark_NoSlicing;
import Paper.Offline.SlicerBenchmark;
import Paper.Online.OnlinePhaseForCanete;
import Paper.Online.OnlinePhaseForHandcraftedFMs;
import Paper.Online.OnlySATSolver;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class SmokeTestRunner {
    private static final String threshold = "10";
    private static final String fmJson = "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json";
    private static final String maxRequirements = "14";
    private static final String edgeIndex = "1";


    public static void main(String[] args) throws RunnerException {


        var opt1 = new OptionsBuilder()
                .include(ConfigurationCalculatorBenchmark.class.getSimpleName())
                .param("_FilePathFM", fmJson)
                .param("_Thresholds", threshold)
                // Specify the output file for the results
                .result("smoke_configurationCalculator.csv")
                .measurementIterations(5)
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
                .param("_FilePathFM", fmJson)
                .param("_Thresholds", threshold)
                // Specify the output file for the results
                .result("smoke_slicerBenchmark.csv")
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
                .include(ConfigurationCalculatorBenchmark_NoSlicing.class.getSimpleName())
                .param("_FilePathFM", fmJson)
                .timeout(TimeValue.hours(1))
                // Specify the output file for the results
                .result("smoke_configurationCalculator_NoSlicing.csv")
                .measurementIterations(3)
                .warmupIterations(3)
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        var opt4 = new OptionsBuilder()
                .result("smoke_result_canete.csv")
                .forks(1)
                .threads(4)
                .resultFormat(ResultFormatType.CSV)
                .include(OnlinePhaseForCanete.class.getSimpleName())
                .measurementIterations(5)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                .param("_FilePathEdgeNodes", "EdgeNodes_CountrySide.json")
                .param("_FilePathFM", "FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json")
                .param("_MaxRequirements", maxRequirements)
                .build();

        var opt5 = new OptionsBuilder()
                .include(OnlinePhaseForHandcraftedFMs.class.getSimpleName() + ".onlinePhaseBenchmark")
                .param("_FilePathFM", fmJson)
                .param("_MaxRequirements", maxRequirements)
                .param("_EdgeIndex", edgeIndex)
                // Specify the output file for the results
                .result("smoke_onlineBenchmark.csv")
                .measurementIterations(5)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();

        var opt6 = new OptionsBuilder()
                .include(OnlinePhaseForHandcraftedFMs.class.getSimpleName() + ".onlinePhaseBenchmarkLessIterations")
                .param("_FilePathFM", fmJson)
                .param("_MaxRequirements", maxRequirements)
                .param("_EdgeIndex", edgeIndex)
                // Specify the output file for the results
                .result("smoke_onlineBenchmarkHuge.csv")
                .measurementIterations(5)
                .warmupIterations(3)
                .timeout(TimeValue.minutes(10))
                // Set the result format to CSV
                .resultFormat(ResultFormatType.CSV)
                // Set the number of forks
                .forks(1)
                .threads(4)
                .build();


        new Runner(opt1).run();
        new Runner(opt2).run();
        new Runner(opt3).run();
        new Runner(opt4).run();
        new Runner(opt5).run();
        new Runner(opt6).run();
    }
}
