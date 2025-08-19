package MeasurementUnit;

import Monitoring.Event.IMeasurementUpdate;
import Monitoring.MeasurementUnit.impl.NetworkMeasurement;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class NetworkMeasurement_Test {
    @Test
    public void unregisterMeasurementUpdateNotification_NoException() {
        var instanceUnderTest = new NetworkMeasurement();
        var mockedEventHandler = mock(IMeasurementUpdate.class);

        instanceUnderTest.unregisterMeasurementUpdateNotification(mockedEventHandler);
    }
}