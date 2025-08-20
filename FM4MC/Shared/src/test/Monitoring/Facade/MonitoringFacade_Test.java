package Monitoring.Facade;

import Monitoring.Facade.impl.MonitoringFacade;
import Monitoring.Event.IMeasurementUpdate;
import Monitoring.MeasurementUnit.impl.NetworkMeasurement;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class MonitoringFacade_Test {

    @Test
    public void registerAndUnregisterDelegatesToNetworkMeasurement() throws Exception {
        var facade = new MonitoringFacade();
        var notifier = mock(IMeasurementUpdate.class);

        Field field = MonitoringFacade.class.getDeclaredField("_networkMeasurement");
        field.setAccessible(true);
        NetworkMeasurement measurement = (NetworkMeasurement) field.get(facade);

        Field listField = NetworkMeasurement.class.getDeclaredField("_registeredMeasurementUpdateConsumer");
        listField.setAccessible(true);
        List<?> list = (List<?>) listField.get(measurement);

        assertEquals(0, list.size());
        facade.registerMeasurementUpdateNotification(notifier);
        assertEquals(1, list.size());
        facade.unregisterMeasurementUpdateNotification(notifier);
        assertEquals(0, list.size());
    }
}
