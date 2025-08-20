package IO;

import IO.impl.LatencyTrace;
import consts.Consts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LatencyTrace_Test {

    @Test
    public void constructors_SetFields() {
        var trace = new LatencyTrace("id", 0L, 1024L, 1_000_000L, "dest");
        assertEquals("id", trace.ID());
        assertEquals(0L, trace.getStartTime());
        assertEquals(1024L, trace.getDataSizeInByte());
        assertEquals(1_000_000L, trace.getEndTime());
        assertEquals("dest", trace.getDestination());
    }

    @Test
    public void setters_UpdateValues() {
        var trace = new LatencyTrace("id", 0L, 1L);
        trace.setEndTime(2_000_000L);
        trace.setDestination("new");
        assertEquals("new", trace.getDestination());
        assertEquals(2_000_000L, trace.getEndTime());
        assertEquals((double)(2_000_000L - 0L) / Consts.NanoSecToMilli, trace.getTimeTakenInMillis());
        assertTrue(trace.getInfoForLogging().contains("new"));
    }
}
