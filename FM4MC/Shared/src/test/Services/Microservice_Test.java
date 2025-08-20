package Services;

import Network.DataModel.CommunicationMessages;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Microservice_Test {

    @Test
    public void getPort_KnownPurpose_ReturnsPort() {
        var port = CommunicationMessages.PurposePort.newBuilder()
                .setPurpose("http")
                .setPort(8080)
                .build();
        var service = new Microservice("id", "127.0.0.1", List.of(port), 9090);

        assertEquals(8080, service.getPort("http"));
    }

    @Test
    public void getPort_UnknownPurpose_ReturnsMinusOne() {
        var service = new Microservice("id", "127.0.0.1", List.of(), 9090);

        assertEquals(-1, service.getPort("ftp"));
    }

    @Test
    public void allocateAndRelease_UpdateState() {
        var service = new Microservice("id", "127.0.0.1", List.of(), 9090);

        service.allocate();
        assertEquals(ServiceState.PENDING, service.getState());

        service.release();
        assertEquals(ServiceState.FREE, service.getState());
    }

    @Test
    public void loadExecution_SetsExecutionInfo() {
        var service = new Microservice("id", "127.0.0.1", List.of(), 9090);
        service.loadExecution("run");

        assertEquals("run", service.getExecution());
    }

    @Test
    public void constructorWithExecution_SetsExecution() {
        var service = new Microservice("id", "127.0.0.1", List.of(), "exec", 9090);

        assertEquals("exec", service.getExecution());
        assertEquals(9090, service.getHandlerPort());
        assertEquals("127.0.0.1", service.getAddress());
        assertEquals("id", service.ID());
    }
}
