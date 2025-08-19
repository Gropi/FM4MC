package SlicingBenchmark;

import ConfigurationCalculator.ConfigurationCalculator;
import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class NoSlicingJobSAT implements Callable<FeatureModelPartiallyCalculated> {

    private ConfigurationCalculator calculator;
    private FeatureModelRead fmRead;

    public NoSlicingJobSAT(Logger logger, FeatureModelRead fmRead) {
        this.calculator = new ConfigurationCalculator(logger);
        this.fmRead = fmRead;
    }

    @Override
    public FeatureModelPartiallyCalculated call() throws Exception {
        return calculator.calculatedConfigurationForNonSlicedFM(fmRead);
    }
}
