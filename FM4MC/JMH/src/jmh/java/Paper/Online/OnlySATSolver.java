package Paper.Online;

import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import SlicingBenchmark.NoSlicingJobSAT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class OnlySATSolver {
    private final Logger _Logger = LogManager.getLogger("executionLog");
    private final int TIMEOUT_IN_SECONDS = 216000;

    @Param({""})
    public String _FilePathFM;

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime) 
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void onlySATSolverHandcraftedFiles(Blackhole blackhole) throws InvalidFeatureModelRelationException {
        var reader = new FeatureModelReader(_Logger);

        var _ReadFeatureModel = reader.readFeatureModelJson(new File(_FilePathFM));
        var executor = Executors.newSingleThreadExecutor();

        var future = executor.submit(new NoSlicingJobSAT(_Logger, _ReadFeatureModel));

        try {
            var fmPartiallyCalculated = future.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            blackhole.consume(fmPartiallyCalculated);
        } catch (Exception e) {
            _Logger.info("Configuration calculation timed out at: " + TIMEOUT_IN_SECONDS + " S");
            future.cancel(true);
        } finally {
            executor.shutdownNow();
        }
        blackhole.consume(future);
        blackhole.consume(_ReadFeatureModel);
    }
}
