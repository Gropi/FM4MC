package Services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceState_Test {

    @Test
    public void of_ReturnsMatchingState() {
        assertEquals(ServiceState.FREE, ServiceState.of(0));
        assertEquals(ServiceState.PENDING, ServiceState.of(1));
        assertEquals(ServiceState.RUNNING, ServiceState.of(2));
        assertEquals(ServiceState.TERMINATED, ServiceState.of(3));
    }

    @Test
    public void of_InvalidValue_Throws() {
        assertThrows(IllegalArgumentException.class, () -> ServiceState.of(-1));
        assertThrows(IllegalArgumentException.class, () -> ServiceState.of(5));
    }

    @Test
    public void getValue_ReturnsNumeric() {
        assertEquals(0, ServiceState.FREE.getValue());
        assertEquals(1, ServiceState.PENDING.getValue());
        assertEquals(2, ServiceState.RUNNING.getValue());
        assertEquals(3, ServiceState.TERMINATED.getValue());
    }
}
