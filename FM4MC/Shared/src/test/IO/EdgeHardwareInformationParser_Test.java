package IO;

import IO.impl.EdgeHardwareInformationParser;
import IO.impl.EdgeNodeHardwareInformation;
import IO.impl.LshwClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeHardwareInformationParser_Test {

    @Test
    public void parseHardwareFromXML_ParsesNodes() throws Exception {
        String xml = "<list><node class=\"processor\"/><node class=\"memory\"/></list>";
        EdgeNodeHardwareInformation info = EdgeHardwareInformationParser.parseHardwareFromXML(xml);
        assertNotNull(info);
        for (var cls : LshwClass.values()) {
            assertNotNull(info.hardwareInformationMap.get(cls));
        }
    }

    @Test
    public void parseHardwareFromXML_IgnoresPrefix() throws Exception {
        String xml = "noise<list></list>";
        EdgeNodeHardwareInformation info = EdgeHardwareInformationParser.parseHardwareFromXML(xml);
        assertNotNull(info);
    }
}
