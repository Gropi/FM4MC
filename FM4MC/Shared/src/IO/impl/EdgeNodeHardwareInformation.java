package IO.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeNodeHardwareInformation {

    public Map<LshwClass, Integer> hardwareInformationMap;

    public EdgeNodeHardwareInformation() {
        this.hardwareInformationMap = new HashMap<>();
        for (var lshwClass : LshwClass.values()) {
            this.hardwareInformationMap.put(lshwClass, 0);
        }
    }

}
