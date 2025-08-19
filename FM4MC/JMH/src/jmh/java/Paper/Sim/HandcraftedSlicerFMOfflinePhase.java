package Paper.Sim;

import ConfigurationCalculator.ConfigurationCalculator;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.FeatureModelSlicer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1)
public class HandcraftedSlicerFMOfflinePhase {
    private final Logger _Logger = LogManager.getLogger("executionLog");

    @Param({
            "./TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json",
            "./TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json",
            "./TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json",
            "./TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json"
    })
    public String _FilePathFM;

    @Param({"3", "4", "5", "6", "7", "8", "9", "10"})
    public int _Thresholds;

    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);

    private FeatureModelRead _ReadFeatureModel = null;

    @Setup(Level.Trial)
    public void initTests() throws InvalidFeatureModelRelationException {
        _ReadFeatureModel = fmReader.readFeatureModelJson(new File(_FilePathFM));
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 30)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void sliceFMBenchmark(Blackhole blackhole)  {
        var slicer = new FeatureModelSlicer(_Logger);
        var calculator = new ConfigurationCalculator(_Logger);
        var slices = slicer.sliceFeatureModel(_ReadFeatureModel, _Thresholds);
        blackhole.consume(calculator.calculatePartialConfigurations(slices));
        blackhole.consume(slicer);
    }
}
