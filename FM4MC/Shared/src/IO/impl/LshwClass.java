package IO.impl;

public enum LshwClass {
    DISPLAY(0),
    PROCESSOR(1),
    MEMORY(2),
    NETWORK(3),
    TENSOR(4),
    NEURAL(5),
    STORAGE(6),
    PRICE(7),
    ENERGY(8),
    CAMERA(9),
    MICROPHONE(10),
    SENSOR(11),
    BLE(12),
    WIFI(13);

    private final int _Value;

    LshwClass (int value) {
        _Value = value;
    }
    public int getValue() {return _Value; }
}
