package BusinessLogic;

import Network.Connection.IConnectionInformation;
import Network.DataModel.CommunicationMessages;

public interface IApplication extends Runnable {

    boolean isRunning();

    void onMeasurementUpdate(CommunicationMessages.MeasurementEvent measurementEvent, IConnectionInformation from);

    boolean onMicroserviceTerminated(CommunicationMessages.TerminationMessage terminationMessage);

    void exit();

}
