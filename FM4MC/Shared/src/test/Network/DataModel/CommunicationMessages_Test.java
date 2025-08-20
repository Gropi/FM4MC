package Network.DataModel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommunicationMessages_Test {

    @Test
    public void types_forNumber_ReturnsEnum() {
        assertEquals(CommunicationMessages.Types.CPU, CommunicationMessages.Types.forNumber(1));
        assertNull(CommunicationMessages.Types.forNumber(99));
    }

    @Test
    public void networkRequestType_forNumber_ReturnsEnum() {
        assertEquals(CommunicationMessages.NetworkRequestType.TCP, CommunicationMessages.NetworkRequestType.forNumber(1));
        assertNull(CommunicationMessages.NetworkRequestType.forNumber(99));
    }
}
