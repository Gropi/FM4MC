package Network.DataModel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HardwareInformationMessages_Test {

    @Test
    public void forNumber_ReturnsEnum() {
        assertEquals(HardwareInformationMessages.HardwareRequestTypes.JSON,
                HardwareInformationMessages.HardwareRequestTypes.forNumber(1));
        assertNull(HardwareInformationMessages.HardwareRequestTypes.forNumber(99));
    }

    @Test
    public void builder_CreatesMessage() {
        var info = HardwareInformationMessages.HardwareInformation.newBuilder()
                .setHardwareDescription("desc")
                .build();
        assertEquals("desc", info.getHardwareDescription());
    }
}
