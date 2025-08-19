package IO.impl;

import IO.ILatencyTrace;
import consts.Consts;

import java.util.Formatter;

public class LatencyTrace implements ILatencyTrace {
    private String m_ID;
    private long m_StartTimeInNano;
    private long m_EndTimeInNano;
    private long m_DataSizeInByte;

    private String m_destination;

    public LatencyTrace(String messageId, long startTime, long dataSetSizeInByte) {
        m_ID = messageId;
        m_StartTimeInNano = startTime;
        m_DataSizeInByte = dataSetSizeInByte / 1024 / 1024;
    }

    public LatencyTrace(String messageId, long startTimeInNano, long dataSetSizeInByte, long endTimeInNano, String destination) {
        m_ID = messageId;
        m_StartTimeInNano = startTimeInNano;
        m_DataSizeInByte = dataSetSizeInByte ;
        m_EndTimeInNano = endTimeInNano;
        m_destination = destination;
    }

    public String ID() {
        return m_ID;
    }

    public long getStartTime() {
        return m_StartTimeInNano;
    }

    public double getDataSizeInMB() {
        return (double)m_DataSizeInByte / 1024 / 1024;
    }

    public long getDataSizeInByte() {
        return m_DataSizeInByte;
    }

    public long getEndTime() {
        return m_EndTimeInNano;
    }

    public void setEndTime(long endTime) {
        m_EndTimeInNano = endTime;
    }

    public double getTimeTakenInMillis() {
        return (double)(m_EndTimeInNano - m_StartTimeInNano) / Consts.NanoSecToMilli;
    }

    public String getDestination(){ return m_destination; }

    public void setDestination(String destination){
        m_destination = destination;
    }


    @Override
    public String getInfoForLogging() {
        var fm = new Formatter();
        fm.format("%.4f", getTimeTakenInMillis());
        return "[" + fm + "," + getDataSizeInByte() + "," + getDestination() + "]";
    }
}
