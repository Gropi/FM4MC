package IO;

import IO.impl.LshwClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LshwClass_Test {

    @Test
    public void getValue_ReturnsAssignedValue() {
        assertEquals(0, LshwClass.DISPLAY.getValue());
        assertEquals(13, LshwClass.WIFI.getValue());
    }
}
