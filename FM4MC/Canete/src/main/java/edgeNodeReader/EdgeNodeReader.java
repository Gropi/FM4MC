package edgeNodeReader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edgeNodeReader.structures.*;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class EdgeNodeReader {
    private final Logger _Logger;

    public EdgeNodeReader(Logger logger) {
        _Logger = logger;
    }

    public List<EdgeNode> readEdgeNodeJson(File file) {

        FileReader fileReader = null;

        try {
            fileReader = new FileReader(file);
        } catch (Exception e) {
            _Logger.error(e);
            return null;
        }

        var gson = new Gson();
        var jsonObject = gson.fromJson(fileReader, JsonObject.class);
        var edgeNodesJsonArray = jsonObject.getAsJsonArray("edgeNodes");
        var edgeNodes = new ArrayList<EdgeNode>();

        for (var edgeNodeJson : edgeNodesJsonArray) {
            var edgeNodeObject = edgeNodeJson.getAsJsonObject();

            int cpu = edgeNodeObject.get("cpu").getAsInt();
            int bandwidth = edgeNodeObject.get("bandwidth").getAsInt();
            int ram = edgeNodeObject.get("ram").getAsInt();
            int gpu = edgeNodeObject.get("gpu").getAsInt();
            String id = edgeNodeObject.get("id").getAsString();
            int tpu = edgeNodeObject.get("tpu").getAsInt();
            int npu = edgeNodeObject.get("npu").getAsInt();
            int storage = edgeNodeObject.get("storage").getAsInt();
            int price = edgeNodeObject.get("price").getAsInt();
            int energy = edgeNodeObject.get("energy").getAsInt();
            int camera = edgeNodeObject.get("camera").getAsInt();
            int microphone = edgeNodeObject.get("microphone").getAsInt();
            int sensor = edgeNodeObject.get("sensor").getAsInt();
            int ble = edgeNodeObject.get("ble").getAsInt();
            int wifi = edgeNodeObject.get("wifi").getAsInt();

            edgeNodes.add(new EdgeNode(cpu, bandwidth, ram, gpu, tpu, npu, storage, price, energy, camera, microphone, sensor, ble, wifi, id));
            /*
            Set<PeripheralType> peripherals = new HashSet<>();
            for (var peripheralJson : edgeNodeObject.getAsJsonArray("peripherals")) {
                switch (peripheralJson.getAsString()) {
                    case "camera" -> peripherals.add(PeripheralType.CAMERA);
                    case "microphone" -> peripherals.add(PeripheralType.MICROPHONE);
                }
            }

            Set<SensingUnitType> sensingUnits = new HashSet<>();
            for (var sensingUnitJson : edgeNodeObject.getAsJsonArray("sensingUnits")) {
                switch (sensingUnitJson.getAsString()) {
                    case "parking" -> sensingUnits.add(SensingUnitType.PARKING);
                    case "presence" -> sensingUnits.add(SensingUnitType.PRESENCE);
                    case "temperature" -> sensingUnits.add(SensingUnitType.TEMPERATURE);
                    case "humidity" -> sensingUnits.add(SensingUnitType.HUMIDITY);
                    case "wind" -> sensingUnits.add(SensingUnitType.WIND);
                }
            }

            NodeType type;
            switch (edgeNodeObject.get("type").getAsString()) {
                case "computing" -> type = NodeType.COMPUTING;
                case "sensing" -> type = NodeType.SENSING;
                default -> type = NodeType.COMPUTING;
            }

            OwnerType owner;
            switch (edgeNodeObject.get("owner").getAsString()) {
                case "public" -> owner = OwnerType.PUBLIC;
                default -> owner = OwnerType.PUBLIC;
            }

            Set<ConnectionType> connectionTypes = new HashSet<>();
            for (var connectionTypeJson : edgeNodeObject.getAsJsonArray("connectionTypes")) {
                switch (connectionTypeJson.getAsString()) {
                    case "wlan" -> connectionTypes.add(ConnectionType.WLAN);
                    case "ble" -> connectionTypes.add(ConnectionType.BLE);
                }
            }*/


        }


        return edgeNodes;
    }

}