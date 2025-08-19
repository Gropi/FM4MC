package Services;

/**
 * Enumeration of possible lifecycle states for a microservice.
 */
public enum ServiceState {
    FREE(0), PENDING(1), RUNNING(2), TERMINATED(3);
    int _Value;

    ServiceState(int value) {
        _Value = value;
    }

    /**
     * Numeric representation of the state used in communication messages.
     *
     * @return integer value of the state
     */
    public int getValue(){
        return _Value;
    }

    /**
     * Resolves a {@link ServiceState} from its numeric value.
     *
     * @param value integer representing a state
     * @return matching {@link ServiceState}
     * @throws IllegalArgumentException if the value does not correspond to any state
     */
    public static ServiceState of(int value) {
        for (ServiceState serviceState : values()) {
            if (serviceState._Value == value) return serviceState;
        }
        throw new IllegalArgumentException("There is no state for the value  " + value);
    }
}
