package Paper.Online;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class CaneteBenchmarkRunner {
    public static void main(String[] args) throws RunnerException {
        var opt = new OptionsBuilder()
                .result("result_canete.csv")
                .threads(4)
                .resultFormat(ResultFormatType.CSV)
                .include(OnlinePhaseForCanete.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
