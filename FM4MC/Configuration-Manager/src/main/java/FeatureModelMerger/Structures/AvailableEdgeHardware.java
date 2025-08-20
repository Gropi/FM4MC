package FeatureModelMerger.Structures;

import IO.impl.LshwClass;

import java.util.LinkedHashMap;
import java.util.Map;

public class AvailableEdgeHardware {

    public Map<LshwClass, Integer> edgeHardware = new LinkedHashMap<>();

    public AvailableEdgeHardware() {
        for (var lshwClass : LshwClass.values()) {
            edgeHardware.put(lshwClass, 0);
        }
    }

    public AvailableEdgeHardware(int x) {
        for (var lshwClass : LshwClass.values()) {
            edgeHardware.put(lshwClass, x);
        }
    }
}
