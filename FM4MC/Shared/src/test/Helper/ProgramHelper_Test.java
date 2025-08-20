package Helper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

public class ProgramHelper_Test {

    @Test
    public void debugFlagReflectsDebuggerStatus() throws Exception {
        Method method = ProgramHelper.class.getDeclaredMethod("isDebuggerAttached");
        method.setAccessible(true);
        boolean expected = (boolean) method.invoke(null);
        assertEquals(expected, ProgramHelper.DEBUG);
        assertFalse(ProgramHelper.DEBUG);
    }
}
