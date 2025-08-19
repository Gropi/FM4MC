package Network.Facade.impl;

import Monitoring.Facade.IMonitoringFacade;
import Network.Connection.IClient;
import Network.Connection.ICommunication;
import Network.Connection.IConnectionInformation;
import Network.Connection.IServer;
import Network.Connection.impl.Client;
import Network.Connection.impl.ConnectionInformation;
import Network.Connection.impl.Server;
import Network.Facade.IConnectionFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ConnectionFacade implements IConnectionFacade {
    private static final Logger _Logger = LogManager.getRootLogger();
    private final Map<Integer, IServer> _CurrentlyRunningServer;

    public ConnectionFacade() {
        _CurrentlyRunningServer = new HashMap();
    }

    @Override
    public IServer startServer(int port) {
        IServer communication;
        if (_CurrentlyRunningServer.containsKey(port)) {
            communication = _CurrentlyRunningServer.get(port);
            _Logger.debug("Took server from already running for port: " + port);
        } else {
            communication = new Server(port);
            _CurrentlyRunningServer.put(port, communication);
            _Logger.debug("Started server on port: " + port + " successfully.");
        }
        return communication;
    }

    @Override
    public IClient startClient(String serverIPAddress, int port) {
        var connectionInformation = new ConnectionInformation(serverIPAddress, port);
        return startClient(connectionInformation);
    }

    @Override
    public IClient startClient(IConnectionInformation connectionInformation) {
        return new Client(connectionInformation);
    }
}
