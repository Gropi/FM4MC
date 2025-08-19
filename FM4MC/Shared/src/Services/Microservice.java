package Services;

import Network.Connection.IConnectionInformation;
import Network.DataModel.CommunicationMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data holder representing a microservice instance with its network
 * configuration and execution state.
 */
public class Microservice {
    private String _ExecutionInformation;
    private ServiceState _State;
    private String _Id;
    private String _IP;
    private Map<String, Integer> _Ports;
    private int _HandlerPort;

    /**
     * Creates a new microservice description.
     *
     * @param id unique identifier of the service
     * @param ip IP address of the service
     * @param purposePorts ports with their corresponding purposes
     * @param handlerPort port used by the handler service
     */
    public Microservice(String id, String ip, List<CommunicationMessages.PurposePort> purposePorts, int handlerPort) {
        _Id = id;
        _IP = ip;
        _State = ServiceState.FREE;

        var ports = new HashMap<String, Integer>();
        for(var purposePort : purposePorts){
            ports.put(purposePort.getPurpose(), purposePort.getPort());
        }
        _Ports = ports;
        _HandlerPort = handlerPort;
    }

    /**
     * Creates a new microservice description including execution information.
     *
     * @param id unique identifier of the service
     * @param ip IP address of the service
     * @param purposePorts ports with their corresponding purposes
     * @param executionInformation additional execution information
     * @param handlerPort port used by the handler service
     */
    public Microservice(String id, String ip, List<CommunicationMessages.PurposePort> purposePorts, String executionInformation, int handlerPort){
        this(id, ip, purposePorts, handlerPort);
        _ExecutionInformation = executionInformation;
    }

    /**
     * Returns the current allocation state of the service.
     *
     * @return current {@link ServiceState}
     */
    public ServiceState getState() {
        return _State;
    }

    /**
     * @return unique identifier of the service
     */
    public String ID() {
        return _Id;
    }

    /**
     * @return handler port used by the service
     */
    public int getHandlerPort(){
        return _HandlerPort;
    }

    /**
     * Looks up the port for the specified purpose.
     *
     * @param purpose name of the required port
     * @return port number or -1 if unknown
     */
    public int getPort(String purpose) {
        if(!_Ports.containsKey(purpose))
            return -1;

        return _Ports.get(purpose);
    }

    /**
     * @return IP address of the service
     */
    public String getAddress(){
        return _IP;
    }

    /**
     * Marks the service as allocated and awaiting start up.
     */
    public void allocate() {
        _State = ServiceState.PENDING;
    }

    /**
     * Marks the service as released and free for allocation.
     */
    public void release() {
        _State = ServiceState.FREE;
    }

    /**
     * Stores execution related information for the service.
     *
     * @param execution execution data to attach
     */
    public void loadExecution(String execution) {
        _ExecutionInformation = execution;
    }

    /**
     * @return stored execution information or {@code null}
     */
    public String getExecution() {
        return _ExecutionInformation;
    }
}