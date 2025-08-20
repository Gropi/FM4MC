package consts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Consts_Test {
    @Test
    public void nanoSecToMilli_EqualsOneMillion() {
        assertEquals(1_000_000, Consts.NanoSecToMilli);
    }
}
