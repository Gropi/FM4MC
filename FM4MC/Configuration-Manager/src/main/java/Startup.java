import Businesslogic.CentralManager;
import Console.ThreadSafeConsoleHandler;
import Monitoring.Event.Logging.impl.LogInformation;
import Network.Connection.impl.ConnectionInformation;
import Network.Facade.impl.ConnectionFacade;
import logging.MyLogManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Entry point for the configuration manager. Sets up logging, networking
 * and starts the central manager component.
 */
public class Startup {
    private static final Logger _Logger = LogManager.getLogger("executionLog");

    /**
     * Initializes services and launches the central manager.
     *
     * @param args command line arguments
     * @throws IOException if startup of network components fails
     */
    public static void main(String[] args) throws IOException {
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
