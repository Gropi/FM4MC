package Services;

import Network.Connection.IConnectionInformation;
import Network.DataModel.CommunicationMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Microservice {
    private String _ExecutionInformation;
    private ServiceState _State;
    private String _Id;
    private String _IP;
    private Map<String, Integer> _Ports;
    private int _HandlerPort;

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

    public Microservice(String id, String ip, List<CommunicationMessages.PurposePort> purposePorts, String executionInformation, int handlerPort){
        this(id, ip, purposePorts, handlerPort);
        _ExecutionInformation = executionInformation;
    }

    public ServiceState getState() {
        return _State;
    }

    public String ID() {
        return _Id;
    }

    public int getHandlerPort(){
        return _HandlerPort;
    }

    public int getPort(String purpose) {
        if(!_Ports.containsKey(purpose))
            return -1;

        return _Ports.get(purpose);
    }

    public String getAddress(){
        return _IP;
    }

    public void allocate() {
        _State = ServiceState.PENDING;
    }
    public void release() {
        _State = ServiceState.FREE;
    }

    public void loadExecution(String execution) {
        _ExecutionInformation = execution;
    }

    public String getExecution() {
        return _ExecutionInformation;
    }
}