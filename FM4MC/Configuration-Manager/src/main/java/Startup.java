import BusinessLogic.FeatureModelPreProcessor;
import Businesslogic.CentralManager;
import Businesslogic.CentralManagerSimulation;
import Console.ThreadSafeConsoleHandler;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import IO.impl.LshwClass;
import Monitoring.Event.Logging.impl.LogInformation;
import Network.Connection.impl.ConnectionInformation;
import Network.Facade.impl.ConnectionFacade;
import logging.MyLogManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * Entry point for the configuration manager. Sets up logging, networking
 * and starts the central manager component.
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
        var logManager = new MyLogManager(logParameters);
        logManager.start();

        var arguments = getTestbedParameters(args);
        var processLogic = new CentralManagerSimulation(_Logger);


        if (!arguments.containsKey("configurations")) {
            _Logger.error("Missing argument for configuration input path");
        } else if (!arguments.containsKey("fmFile")) {
            _Logger.error("Missing argument for Feature Model input path");
        } else if (!arguments.containsKey("edgeIndex")) {
            _Logger.error("Missing Edge Index: 1 for tiny, 2 for small, 3 for medium, 4 for big, 5 for huge");
        } else {
            processLogic.startOnlinePhase(arguments.get("configurations"), arguments.get("fmFile"), arguments.get("edgeIndex"));
        }
    }



    /**
     * Parses the command line arguments into a key/value map.
     */
    private static HashMap<String, String> getTestbedParameters(String[] args) {
        var parameters = new HashMap<String, String>();

        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equalsIgnoreCase("-fmFile")) {
                parameters.put("fmFile", args[i + 1]);
            } else if (args[i].equalsIgnoreCase("-configurations")) {
                parameters.put("configurations", args[i + 1]);
            } else if (args[i].equalsIgnoreCase("-edgeIndex")) {
                parameters.put("edgeIndex", args[i + 1]);
            }
        }

        return parameters;
    }


    /**
     * Initializes services and launches the central manager.
     *
     * @param args command line arguments
     * @throws IOException if startup of network components fails
     */
    public static void main2(String[] args) throws IOException {
        var logParameters = new LogInformation(args);
        var logManager = new MyLogManager(logParameters);
        logManager.start();
        var startParameters = new ConnectionInformation(args);
        var consoleHandler = new ThreadSafeConsoleHandler();
        var connectionFacade = new ConnectionFacade();
        var manager = new CentralManager(connectionFacade, startParameters.getManagementPort());
        consoleHandler.addListener(manager);
        consoleHandler.start();
    }
}
