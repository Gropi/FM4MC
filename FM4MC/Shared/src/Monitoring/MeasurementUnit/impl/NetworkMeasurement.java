package Monitoring.MeasurementUnit.impl;

import Monitoring.Enums.MeasurableValues;
import Monitoring.Event.IMeasurementUpdate;
import Monitoring.Facade.IMonitoringFacade;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Measurement unit that exposes network related metrics and notifies
 * registered consumers about updates.
 */
public class NetworkMeasurement implements IMonitoringFacade, Runnable {
    private static final Logger _Logger = LogManager.getLogger(NetworkMeasurement.class);
    private final List<IMeasurementUpdate> _registeredMeasurementUpdateConsumer;
    private final String os = System.getProperty("os.name").toLowerCase();

    /**
     * Creates a new network measurement unit.
     */
    public NetworkMeasurement() {
        _registeredMeasurementUpdateConsumer = new CopyOnWriteArrayList<>();
        _Logger.trace("Initialize network logger ");
    }

    @Override
    public void run() {

    }

    /**
     * Registers a listener to receive measurement updates.
     *
     * @param notifier consumer interested in updates
     */
    @Override
    public void registerMeasurementUpdateNotification(IMeasurementUpdate notifier) {
        if (!_registeredMeasurementUpdateConsumer.contains(notifier))
            _registeredMeasurementUpdateConsumer.add(notifier);
    }

    /**
     * Unregisters a previously registered listener.
     *
     * @param notifier consumer to remove
     */
    @Override
    public void unregisterMeasurementUpdateNotification(IMeasurementUpdate notifier) {
        _registeredMeasurementUpdateConsumer.remove(notifier);
    }

    /**
     * Begins collecting CPU related metrics.
     *
     * @throws IOException if required system commands cannot be executed
     */
    @Override
    public void startCollectingCPU() throws IOException {

    }

    /**
     * Begins collecting RAM related metrics.
     *
     * @throws IOException if required system commands cannot be executed
     */
    @Override
    public void startCollectingRAM() throws IOException {

    }

    private void updateMeasurement(MeasurableValues measuredValue, int newValue) throws IOException {
        for (var listener: _registeredMeasurementUpdateConsumer) {
            // TODO: add identifier
            listener.MeasurementUpdated(measuredValue, newValue, null);
        }
    }

    private String traceRoute(InetAddress address){
        String route = "";
        try {
            Process traceRt;
            if(os.contains("win")) traceRt = Runtime.getRuntime().exec("tracert " + address.getHostAddress());
            else traceRt = Runtime.getRuntime().exec("traceroute " + address.getHostAddress());

            // read the output from the command
            route = convertStreamToString(traceRt.getInputStream());

            // read any errors from the attempted command
            String errors = convertStreamToString(traceRt.getErrorStream());
            if(errors != "") _Logger.error(errors);
        }
        catch (IOException e) {
            _Logger.error("error while performing trace route command", e);
        }

        return route;
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
