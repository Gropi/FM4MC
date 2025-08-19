package IO;

public interface ILatencyTrace {
    /**
     * Returns the id of the trace
     * @return The id of the trace
     */
    String ID();

    /**
     * Gets the start time of the communication. This is needed to calculate the duration later on.
     * @return The start time of the communication in milliseconds
     */
    long getStartTime();

    /**
     * Gets the data size of the package that was send.
     * @return The data size of the package in mb.
     */
    double getDataSizeInMB();

    /**
     * Gets the end time of the communication. This is needed to calculate the duration later on.
     * @return The end time of the communication in milliseconds
     */
    long getEndTime();

    /**
     * Allows you to set the end time of the communication. Needs to be in millis.
     * @param endTime The timestamp of the communication end in millis.
     */
    void setEndTime(long endTime);

    /**
     * Calculates the total taken time for the communication in millis.
     * @return The total time taken for the communication in millis.
     */
    double getTimeTakenInMillis();

    /**
     *
     * @return
     */
    String getInfoForLogging();
}
