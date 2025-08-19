package BusinessLogic.impl;

import Events.IConsoleInputListener;
import Events.IMessageReceivedListener;
import Network.Connection.ICommunication;
import Network.Connection.IConnectionInformation;
import Network.DataModel.HardwareInformationMessages;
import Network.Facade.IConnectionFacade;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Locale;

/**
 * Server component that listens for hardware information requests and forwards
 * the data to an aggregator service.
 */
public class InformationCollector implements IConsoleInputListener, IMessageReceivedListener {
    private static final Logger _Logger = LogManager.getLogger("executionLog");
    private final int _Port;
    private final IConnectionFacade _ConnectionFacade;
    private ICommunication _Communication;
    private Thread _ServerThread;

    /**
     * Creates a new information collector listening on the given port.
     *
     * @param connectionFacade facade used to create network connections
     * @param portToListenOn port on which to listen for incoming requests
     * @throws IOException if the server cannot be started
     */
    public InformationCollector(IConnectionFacade connectionFacade, int portToListenOn) throws IOException {
        _ConnectionFacade = connectionFacade;
        _Port = portToListenOn;
        start();
    }

    private void start() {
        _Logger.debug("Start server with parameter: Port: " + _Port);
        _Communication = _ConnectionFacade.startServer(_Port);
        _Communication.addMessageReceivedListener(this);
        _ServerThread = new Thread(_Communication);
        _ServerThread.start();
    }

    /**
     * Reacts on console input from the user. Currently only the command
     * {@code exit} is supported which terminates the application.
     *
     * @param message user input from the console
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
        }
    }

    /**
     * Handles incoming network messages and responds to hardware information
     * requests.
     *
     * @param message serialized message payload
     * @param from information about the sender
     * @throws IOException if a response cannot be sent
     */
    @Override
    public void messageReceived(byte[] message, IConnectionInformation from) throws IOException {
        _Logger.debug("Connection established from: " + from.getIPAddress());
        if (message != null) {
            try {
                var anyMessage = Any.parseFrom(ByteString.copyFrom(message));
                _Logger.debug("Message received; " + anyMessage);

                if (anyMessage.is(HardwareInformationMessages.HardwareInformationRequest.class)) {
                    var hardwareInformationRequester = anyMessage.unpack(HardwareInformationMessages.HardwareInformationRequest.class);
                    var value = acquireHardwareInformation(hardwareInformationRequester.getType());
                    var availableHardwareInformationMessageBuilder = HardwareInformationMessages.HardwareInformation.newBuilder();
                    availableHardwareInformationMessageBuilder.setHardwareDescription(value);
                    sendAnyMessageToAggregator(Any.pack(availableHardwareInformationMessageBuilder.build()), from, hardwareInformationRequester.getTargetPort());
                }
            } catch (InvalidProtocolBufferException | InterruptedException e) {
                _Logger.error(e.getMessage());
            }
        }
    }

    private <T extends Message> void sendAnyMessageToAggregator(T message, IConnectionInformation to, int port) throws IOException {
        var client = _ConnectionFacade.startClient(to.getIPAddress(), port);
        client.start();
        client.sendMessage(message);
        client.close();
    }

    private String acquireHardwareInformation(HardwareInformationMessages.HardwareRequestTypes type) throws InterruptedException, IOException {
        var processBuilder = new ProcessBuilder();
        var requestType = type == HardwareInformationMessages.HardwareRequestTypes.JSON ? "-json" : "-xml";
        processBuilder.command("sh", "-c", "lshw "+ requestType);
        processBuilder.redirectErrorStream(true);
        var process = processBuilder.start();
        var result = new StringBuilder(1000);

        try (var in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (true) {
                var line = in.readLine();
                if (line == null)
                    break;
                result.append(line);
            }
        }
        return result.toString();
    }
}
