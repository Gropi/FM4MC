package SlicingBenchmark;

public class SlicingBenchmarkRunner {
    public static void main(String[] args) {
        var path = "TestData/TestGraphs/TestFMJsons/";
        var test = new NoJmhSlicingBenchmark();
        test.executeBenchmark(true, true, path);
    }
}