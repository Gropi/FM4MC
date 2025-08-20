package Helper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NumberHelper_Test {

    @Test
    public void compareValues_WorksForIntegersAndDoubles() {
        assertEquals(0, NumberHelper.compareValues(5, 5));
        assertTrue(NumberHelper.compareValues(3, 5) < 0);
        assertTrue(NumberHelper.compareValues(7, 5) > 0);
        assertEquals(0, NumberHelper.compareValues(5.0, 5));
        assertTrue(NumberHelper.compareValues(5, 6.5) < 0);
    }

    @Test
    public void compareValues_IncompatibleTypes_Throws() {
        assertThrows(IllegalArgumentException.class, () -> NumberHelper.compareValues(5, 5L));
    }

    @Test
    public void divideAndMultiply() {
        assertEquals(2.5, NumberHelper.divideValuesAsDoubles(5, 2));
        assertEquals(10.0, NumberHelper.multiplyNumberWithDouble(5, 2.0));
    }

    @Test
    public void addAndSubtract() {
        assertEquals(8, NumberHelper.addValues(5, 3));
        assertEquals(2, NumberHelper.subtractValues(5, 3));
    }

    @Test
    public void addValues_IncompatibleTypes_Throws() {
        assertThrows(IllegalArgumentException.class, () -> NumberHelper.addValues(1, 1L));
    }
}
