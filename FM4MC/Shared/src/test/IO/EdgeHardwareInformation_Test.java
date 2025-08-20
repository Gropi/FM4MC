package IO;

import IO.impl.EdgeHardwareInformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeHardwareInformation_Test {
    @Test
    public void defaultConstructor_EmptyList() {
        var info = new EdgeHardwareInformation();
        assertTrue(info.hardwareInformation.isEmpty());
    }
}
