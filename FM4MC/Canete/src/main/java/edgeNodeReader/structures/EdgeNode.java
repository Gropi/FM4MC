package edgeNodeReader.structures;

public class EdgeNode {

    public int cpu, bandwidth, ram, gpu, tpu, npu, storage, price, energy, camera, microphone, sensor, ble, wifi;
    public String id;

/*
    public EdgeNode(long cpu, long bandWidthUp, double powerUpload, double k, long ram, int policy, double powerDownload, long bandWidthDown, Set<PeripheralType> peripherals, Set<SensingUnitType> sensingUnits, NodeType type, String id, OwnerType owner, Set<ConnectionType> connectionTypes) {
        this.cpu = cpu;
        this.bandwidthUp = bandWidthUp;
        this.powerUpload = powerUpload;
        this.k = k;
        this.ram = ram;
        this.policy = policy;
        this.powerDownload = powerDownload;
        this.bandwidthDown = bandWidthDown;
        this.peripherals = peripherals;
        this.sensingUnits = sensingUnits;
        this.type = type;
        this.id = id;
        this.owner = owner;
        this.connectionTypes = connectionTypes;

 */
public EdgeNode() {

}

    public EdgeNode(int cpu, int bandwidth, int ram, int gpu, int tpu, int npu, int storage, int price, int energy, int camera, int microphone, int sensor, int ble, int wifi, String id) {
        this.cpu = cpu;
        this.bandwidth = bandwidth;
        this.ram = ram;
        this.gpu = gpu;
        this.tpu = tpu;
        this.npu = npu;
        this.storage = storage;
        this.price = price;
        this.energy = energy;
        this.camera = camera;
        this.microphone = microphone;
        this.sensor = sensor;
        this.ble = ble;
        this.wifi = wifi;
        this.id = id;
    }
}
