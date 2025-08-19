package Events.Impl;

import Events.IEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class LatencyEvent implements IEvent {

    public String eventType = new String();
    public ArrayList<Integer> terminationConditions = new ArrayList<Integer>();

    public LatencyEvent(){
        this.eventType = "Latency Event";
        this.terminationConditions = new ArrayList<Integer>(Arrays.asList(1,4,6));
    }

    public ArrayList<Integer> getTerminationConditions() {
        return terminationConditions;
    }

    public String getEventType() {
        return eventType;
    }
}
