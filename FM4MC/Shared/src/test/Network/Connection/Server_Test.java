package Network.Connection;

import Network.Connection.impl.Client;
import Network.Connection.impl.Server;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Server_Test {
    @Test
    public void Constructor_SocketHasNoPort_ExceptionIsThrown() {
    }
    @Test
    public void Test_SendMessage(){

    }
}
