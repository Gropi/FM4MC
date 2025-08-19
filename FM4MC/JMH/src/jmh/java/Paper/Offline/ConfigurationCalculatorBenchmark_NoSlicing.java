package Paper.Offline;

import ConfigurationCalculator.ConfigurationCalculator;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class ConfigurationCalculatorBenchmark_NoSlicing {
    private final Logger _Logger = LogManager.getLogger("executionLog");
    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);
    private FeatureModelRead _NotSliced = null;

    @Param({"../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json"})
    public String _FilePathFM;

    @Setup(Level.Trial)
    public void initTests() throws InvalidFeatureModelRelationException {
        _NotSliced = fmReader.readFeatureModelJson(new File(_FilePathFM));
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void clauseGeneratorFMBenchmarkWithOutSlicing(Blackhole blackhole) {
        var calculator = new ConfigurationCalculator(_Logger);
        blackhole.consume(calculator.calculatedConfigurationForNonSlicedFM(_NotSliced));
        blackhole.consume(calculator);
    }
}
