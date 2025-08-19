package Paper.Offline;

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
public class SlicerBenchmark {
    private final Logger _Logger = LogManager.getLogger("executionLog");
    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);
    private FeatureModelRead _ReadFeatureModel = null;

    @Param({""})
    public String _FilePathFM;

    @Param({"2"})
    public int _Thresholds;

    @Setup(Level.Trial)
    public void initTests() throws InvalidFeatureModelRelationException {
        _ReadFeatureModel = fmReader.readFeatureModelJson(new File(_FilePathFM));
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void sliceFMBenchmark(Blackhole blackhole)  {
        var slicer = new FeatureModelSlicer(_Logger);
        blackhole.consume(slicer.sliceFeatureModel(_ReadFeatureModel, _Thresholds));
        blackhole.consume(slicer);
    }
}
