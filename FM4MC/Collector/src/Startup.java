import BusinessLogic.impl.InformationCollector;
import Network.Connection.impl.ConnectionInformation;
import Console.ThreadSafeConsoleHandler;
import logging.MyLogManager;
import Monitoring.Event.Logging.impl.LogInformation;
import Network.Facade.impl.ConnectionFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Startup {
    private static final Logger _Logger = LogManager.getLogger("executionLog");

    public static void main(String[] args) throws IOException {
        var logParameters = new LogInformation(args);
        var qorLogManager = new MyLogManager(logParameters);
        qorLogManager.start();
        var startParameters = new ConnectionInformation(args);
        var consoleHandler = new ThreadSafeConsoleHandler();
        var connectionFacade = new ConnectionFacade();
        var informationCollector = new InformationCollector(connectionFacade, startParameters.getManagementPort());
        consoleHandler.addListener(informationCollector);
        consoleHandler.start();
    }
}
