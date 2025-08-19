package BusinessLogic;

import Network.Connection.IConnectionInformation;
import Network.DataModel.CommunicationMessages;

/**
 * Contract for applications that can receive monitoring events and interact
 * with microservices.
 */
public interface IApplication extends Runnable {

    /**
     * Indicates whether the application is currently running.
     *
     * @return {@code true} if the application is active
     */
    boolean isRunning();

    /**
     * Handles a measurement update received from a remote microservice.
     *
     * @param measurementEvent event payload containing the measurement
     * @param from connection information about the sender
     */
    void onMeasurementUpdate(CommunicationMessages.MeasurementEvent measurementEvent, IConnectionInformation from);

    /**
     * Notifies the application that a microservice has terminated.
     *
     * @param terminationMessage termination details
     * @return {@code true} if the termination was handled successfully
     */
    boolean onMicroserviceTerminated(CommunicationMessages.TerminationMessage terminationMessage);

    /**
     * Performs a graceful application shutdown.
     */
    void exit();

}
