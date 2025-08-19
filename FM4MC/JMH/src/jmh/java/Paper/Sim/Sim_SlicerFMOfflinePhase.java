package Paper.Sim;

import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.FeatureModelSlicer;
import Helper.LinearFMBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1)
public class Sim_SlicerFMOfflinePhase {
    private final Logger _Logger = LogManager.getLogger("executionLog");

    // Params
    @Param({"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100"})
    public int _Tasks;

    @Param({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"})
    public int _Alternatives;

    @Param({"10", "25", "50", "75", "100", "150", "200", "250", "300", "350", "400", "450", "500", "550", "600", "650", "700", "750", "800", "850", "900", "950", "1000"})
    public int _Thresholds;

    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);

    private final LinearFMBuilder _LinearFMBuilder = new LinearFMBuilder();
    private FeatureModelRead _ReadFeatureModel = null;

    @Setup(Level.Trial)
    public void initTests() throws InvalidFeatureModelRelationException {
        // Setup test data
        _LinearFMBuilder.createLinearFM(_Tasks, _Alternatives);

        _ReadFeatureModel = fmReader.readFeatureModelJson(_LinearFMBuilder.FM_FILE);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime) //Mode.All
    @Warmup(iterations = 3)
    @Measurement(iterations = 30)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void sliceFMBenchmark(Blackhole blackhole)  {
        var slicer = new FeatureModelSlicer(_Logger);
        blackhole.consume(slicer.sliceFeatureModel(_ReadFeatureModel, _Thresholds));
        blackhole.consume(slicer);
    }
}
