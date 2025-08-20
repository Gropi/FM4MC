package IO;

import IO.impl.EdgeNodeHardwareInformation;
import IO.impl.LshwClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeNodeHardwareInformation_Test {

    @Test
    public void constructor_InitializesMap() {
        var info = new EdgeNodeHardwareInformation();
        for (var value : LshwClass.values()) {
            assertEquals(0, info.hardwareInformationMap.get(value));
        }
    }
}
