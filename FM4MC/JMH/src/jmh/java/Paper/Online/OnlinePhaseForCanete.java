package Paper.Online;


import CNFClauseGenerator.CNFClauseGenerator;
import ConfigurationCalculator.ConfigurationCalculator;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;
import edgeNodeReader.EdgeNodeReader;
import edgeNodeReader.structures.EdgeNode;
import modules.AVA;
import modules.EDAF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1)
public class OnlinePhaseForCanete {

    private final Logger _Logger = LogManager.getRootLogger();

    // Params
    @Param({
            "EdgeNodes_CountrySide.json",
            "EdgeNodes_SmallCity.json",
            "EdgeNodes_Highway.json",
            "EdgeNodes_MediumCity.json",
            "EdgeNodes_Full.json"
    })
    public String _FilePathEdgeNodes;

    @Param({
            "../TestData/EdgeNodeJsons/"
    })
    public String _DirectoryPathEdgeNodes;
    @Param({
            "FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json",
            "FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json",
            "FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json",
            "FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json"
    })
    public String _FilePathFM;

    @Param({
            "../TestData/TestGraphs/TestFMJsons/"
    })
    public String _DirectoryPathFM;

    @Param({"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"})
    public int _MaxRequirements;


    private final EdgeNodeReader edgeNodeReader = new EdgeNodeReader(_Logger);
    private final FeatureModelReader fmReader = new FeatureModelReader(_Logger);
    private FeatureModelRead _ReadFeatureModel = null;
    private File _currentFmFile;
    private File _currentEdgeFile;
    private List<EdgeNode> _edgeNodes = null;
    private EdgeNode[] _edgeNodesArray;

    @Setup(Level.Trial)
    public void initTests() throws InvalidFeatureModelRelationException {
        _currentFmFile = new File(_DirectoryPathFM + _FilePathFM);
        _ReadFeatureModel = fmReader.readFeatureModelJson(_currentFmFile);
        _currentEdgeFile = new File(_DirectoryPathEdgeNodes + _FilePathEdgeNodes);
        _edgeNodes = edgeNodeReader.readEdgeNodeJson(_currentEdgeFile);
        _edgeNodesArray = new EdgeNode[_edgeNodes.size()];
        _edgeNodes.toArray(_edgeNodesArray);
    }

    @State(Scope.Thread)
    public static class MyBenchmarkState {

        ConfigurationCalculator calculator;

        @Setup(Level.Trial)
        public void setup() {
            calculator = new ConfigurationCalculator(LogManager.getRootLogger());
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.SingleShotTime}) //Mode.All
    @Warmup(iterations = 3)
    @Measurement(iterations = 30)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void caneteBenchmarkSingleDeployments(MyBenchmarkState state, Blackhole blackhole) {

        var nonPartialFM = new FeatureModelRead(_ReadFeatureModel);

        var ava = new AVA();
        var crossModelConstraints = new HashMap<String, List<Number>>();
        var invalidFeatures = ava.adaptApplication(_edgeNodesArray, nonPartialFM, _MaxRequirements);

        for (Feature feature : invalidFeatures) {
            nonPartialFM.features.remove(feature);
        }

        var calculatedFM = state.calculator.calculatedConfigurationForNonSlicedFM(nonPartialFM);

        var edaf = new EDAF();

        calculatedFM.configurationsPerPartialFeatureModel.getFirst().forEach(configuration -> {
            blackhole.consume(edaf.calculateTaskDeployment(_edgeNodesArray, configuration, calculatedFM, true, _MaxRequirements));
        });

        blackhole.consume(edaf);
        blackhole.consume(calculatedFM);
        blackhole.consume(invalidFeatures);
        blackhole.consume(crossModelConstraints);
        blackhole.consume(ava);
        blackhole.consume(nonPartialFM);
    }
}