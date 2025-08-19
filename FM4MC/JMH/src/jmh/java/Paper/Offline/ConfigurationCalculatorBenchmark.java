package Paper.Offline;

import ConfigurationCalculator.ConfigurationCalculator;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelSlicer.FeatureModelSlicer;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class ConfigurationCalculatorBenchmark {
    private final Logger _Logger = LogManager.getRootLogger();

    @Param({""})
    public String _FilePathFM;

    @Param({"2"})
    public int _Thresholds;

    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);
    private final FeatureModelSlicer fmSlicer = new FeatureModelSlicer(_Logger);
    private FeatureModelSliced _SlicedFeatureModel = null;

    @Setup(Level.Trial)
    public void initTests() throws InvalidFeatureModelRelationException {
        var _ReadFeatureModel = fmReader.readFeatureModelJson(new File(_FilePathFM));
        _SlicedFeatureModel = fmSlicer.sliceFeatureModel(_ReadFeatureModel, _Thresholds);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void partialFeatureClauseGenerationBenchmark(Blackhole blackhole) {
        var calculator = new ConfigurationCalculator(_Logger);
        blackhole.consume(calculator.calculatePartialConfigurations(_SlicedFeatureModel));
        blackhole.consume(calculator);
    }
}
