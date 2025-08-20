package Businesslogic;

import Events.IConsoleInputListener;
import Events.IMessageReceivedListener;
import IO.impl.EdgeHardwareInformationParser;
import Network.Connection.ICommunication;
import Network.Connection.IConnectionInformation;
import Network.Connection.impl.ConnectionInformation;
import Network.DataModel.HardwareInformationMessages;
import Network.Facade.IConnectionFacade;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Locale;

/**
 * Central server component that requests hardware information from edge devices
 * and processes incoming messages.
 */
public class CentralManager implements IMessageReceivedListener, IConsoleInputListener {
    private static final Logger _Logger = LogManager.getLogger("executionLog");
    private final int _Port;
    private final IConnectionFacade _ConnectionFacade;
    private ICommunication _Communication;
    private Thread _ServerThread;
    private final IConnectionInformation test = new ConnectionInformation("130.83.163.46", 2000);

    /**
     * Starts the manager and initiates a hardware information request.
     *
     * @param connectionFacade facade used to create network connections
     * @param portToListenOn port to listen for incoming messages
     * @throws IOException if networking components fail to start
     */
    public CentralManager(IConnectionFacade connectionFacade, int portToListenOn) throws IOException {
        _ConnectionFacade = connectionFacade;
        _Port = portToListenOn;
        start();

        requestEdgeInformation(test);
    }

    /**
     * Handles incoming hardware information messages from edge devices.
     *
     * @param message serialized payload from the sender
     * @param from connection information of the sender
     * @throws IOException if response transmission fails
     */
    @Override
    public void messageReceived(byte[] message, IConnectionInformation from) throws IOException {
        _Logger.debug("New connection from server");
        if (message != null) {
            try {
                var anyMessage = Any.parseFrom(ByteString.copyFrom(message));
                _Logger.debug("Message received; " + anyMessage);

                if (anyMessage.is(HardwareInformationMessages.HardwareInformation.class)) {
                    var hardwareInformationRequester = anyMessage.unpack(HardwareInformationMessages.HardwareInformation.class);
                    var description = hardwareInformationRequester.getHardwareDescription();
                    var edgeNodeInformation = EdgeHardwareInformationParser.parseHardwareFromXML(description);
                    // TODO: process hardware information
                }
            } catch (InvalidProtocolBufferException | ParserConfigurationException | SAXException e) {
                _Logger.error(e.getMessage());
            }
        }
    }

    private void start() {
        _Logger.debug("Start server with parameter: Port: " + _Port);
        _Communication = _ConnectionFacade.startServer(_Port);
        _Communication.addMessageReceivedListener(this);
        _ServerThread = new Thread(_Communication);
        _ServerThread.start();
    }

    /**
     * Processes console commands for shutting down or reconnecting.
     *
     * @param message console command entered by the user
     */
    @Override
    public void HandleConsoleInput(String message) {
        message = message.toLowerCase(Locale.ROOT).trim();

        if (message.equals("exit")) {
            if (_Communication != null) {
                try {
                    _Communication.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.exit(0);
        } else if (message.equals("reconnect")) {
            try {
                requestEdgeInformation(test);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a request to an edge node to obtain hardware information.
     *
     * @param edgeConnectionInformation address of the target edge device
     * @throws IOException if the request cannot be sent
     */
    private void requestEdgeInformation(IConnectionInformation edgeConnectionInformation) throws IOException {
        var edgeRequest = HardwareInformationMessages.HardwareInformationRequest.newBuilder();
        edgeRequest.setTargetPort(_Port);
        edgeRequest.setType(HardwareInformationMessages.HardwareRequestTypes.XML);
        sendAnyMessageToAggregator(Any.pack(edgeRequest.build()), edgeConnectionInformation);
    }

    private <T extends Message> void sendAnyMessageToAggregator(T message, IConnectionInformation to) throws IOException {
        var client = _ConnectionFacade.startClient(to.getIPAddress(), to.getManagementPort());
        client.start();
        client.sendMessage(message);
        client.close();
    }
}
