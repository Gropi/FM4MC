import BusinessLogic.FeatureModelPreProcessor;
import Monitoring.Event.Logging.impl.LogInformation;
import logging.MyLogManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Command line entry point that parses arguments and triggers the feature model
 * preprocessing pipeline.
 */
public class Startup {
    private static final Logger _Logger = LogManager.getLogger("executionLog");

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        var logParameters = new LogInformation(args);
        var qorLogManager = new MyLogManager(logParameters);
        qorLogManager.start();

        var arguments = getTestbedParameters(args);
        var threshold = arguments.containsKey("threshold") ? Integer.parseInt(arguments.get("threshold")) : 250;
        var slicing = arguments.containsKey("slicing");
        if (!slicing)
            threshold = Integer.MAX_VALUE;
        var output = arguments.get("destinationConfigurations");

        var processLogic = new FeatureModelPreProcessor(_Logger);
        if (arguments.containsKey("path")) {
            processLogic.startTestForFolder(arguments.get("path"), threshold, slicing, output);
        } else if (arguments.containsKey("file")) {
            processLogic.startTestForFile(arguments.get("file"), threshold, slicing, output);
        }
    }

    /**
     * Parses the command line arguments into a key/value map.
     */
    private static HashMap<String, String> getTestbedParameters(String[] args) {
        var parameters = new HashMap<String, String>();

        for(int i = 0; i < args.length; i += 2) {
            if(args[i].equalsIgnoreCase("-path")){
                parameters.put("path", args[i + 1]);
            }
            else if(args[i].equalsIgnoreCase("-file")){
                parameters.put("file", args[i + 1]);
            }
            else if(args[i].equalsIgnoreCase("-threshold")){
                parameters.put("threshold", args[i + 1]);
            }
            else if(args[i].equalsIgnoreCase("-slicing")){
                parameters.put("slicing", "true");
                i--;
            }
            else if(args[i].equalsIgnoreCase("-destConfigurations")){
                parameters.put("destinationConfigurations", args[i + 1]);
            }
        }

        return parameters;
    }
}
