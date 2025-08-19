package Helper;

import FeatureModelMerger.Structures.AvailableEdgeHardware;
import IO.impl.LshwClass;
import edgeNodeReader.structures.EdgeNode;

import java.util.Arrays;

public class EdgeTransformer {

    public AvailableEdgeHardware transformNodes(EdgeNode[] edgeNodes) {
        var res = new AvailableEdgeHardware();
        int gpu = 0, cpu = 0, memory = 0, network = 0, tensor = 0, neural = 0, storage = 0, price = 0,
                energy = 0, camera = 0, microphone = 0, sensor = 0, ble = 0, wifi = 0;

        gpu = Arrays.stream(edgeNodes).max((x, y) -> (x.gpu > y.gpu) ? 1 : -1).get().gpu;
        cpu = Arrays.stream(edgeNodes).max((x, y) -> (x.cpu > y.cpu) ? 1 : -1).get().cpu;
        memory = Arrays.stream(edgeNodes).max((x, y) -> (x.ram > y.ram) ? 1 : -1).get().ram;
        network = Arrays.stream(edgeNodes).max((x, y) -> (x.bandwidth > y.bandwidth) ? 1 : -1).get().bandwidth;
        tensor = Arrays.stream(edgeNodes).max((x, y) -> (x.tpu > y.tpu) ? 1 : -1).get().tpu;
        neural = Arrays.stream(edgeNodes).max((x, y) -> (x.npu > y.npu) ? 1 : -1).get().npu;
        storage = Arrays.stream(edgeNodes).max((x, y) -> (x.storage > y.storage) ? 1 : -1).get().storage;
        price = Arrays.stream(edgeNodes).max((x, y) -> (x.price > y.price) ? 1 : -1).get().price;
        energy = Arrays.stream(edgeNodes).max((x, y) -> (x.energy > y.energy) ? 1 : -1).get().energy;
        camera = Arrays.stream(edgeNodes).max((x, y) -> (x.camera > y.camera) ? 1 : -1).get().camera;
        microphone = Arrays.stream(edgeNodes).max((x, y) -> (x.microphone > y.microphone) ? 1 : -1).get().microphone;
        sensor = Arrays.stream(edgeNodes).max((x, y) -> (x.sensor > y.sensor) ? 1 : -1).get().sensor;
        ble = Arrays.stream(edgeNodes).max((x, y) -> (x.ble > y.ble) ? 1 : -1).get().ble;
        wifi = Arrays.stream(edgeNodes).max((x, y) -> (x.wifi > y.wifi) ? 1 : -1).get().wifi;

        res.edgeHardware.put(LshwClass.DISPLAY, gpu);
        res.edgeHardware.put(LshwClass.PROCESSOR, cpu);
        res.edgeHardware.put(LshwClass.MEMORY, memory);
        res.edgeHardware.put(LshwClass.NETWORK, network);
        res.edgeHardware.put(LshwClass.TENSOR, tensor);
        res.edgeHardware.put(LshwClass.NEURAL, neural);
        res.edgeHardware.put(LshwClass.STORAGE, storage);
        res.edgeHardware.put(LshwClass.PRICE, price);
        res.edgeHardware.put(LshwClass.ENERGY, energy);
        res.edgeHardware.put(LshwClass.CAMERA, camera);
        res.edgeHardware.put(LshwClass.MICROPHONE, microphone);
        res.edgeHardware.put(LshwClass.SENSOR, sensor);
        res.edgeHardware.put(LshwClass.BLE, ble);
        res.edgeHardware.put(LshwClass.WIFI, wifi);

        return res;
    }
}