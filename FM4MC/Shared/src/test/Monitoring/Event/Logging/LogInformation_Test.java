package Monitoring.Event.Logging;

import Monitoring.Event.Logging.impl.LogInformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogInformation_Test {

    @Test
    public void defaultConstructor_SetsDefaults() {
        var info = new LogInformation();
        assertEquals("log", info.getLogAddress());
        assertEquals("debug", info.getLogLevel());
    }

    @Test
    public void parseFromParameters_OverridesValues() {
        var info = new LogInformation();
        String[] params = {"-l", "mylog", "-loglevel", "info"};
        info.parseFromParameters(params);
        assertEquals("mylog", info.getLogAddress());
        assertEquals("info", info.getLogLevel());
    }

    @Test
    public void parseFromParameters_Null_DoesNothing() {
        var info = new LogInformation(new String[]{"-l", "adr"});
        info.parseFromParameters(null);
        assertEquals("adr", info.getLogAddress());
        assertEquals("debug", info.getLogLevel());
    }

    @Test
    public void constructorWithParameters_AppliesValues() {
        var info = new LogInformation(new String[]{"-l", "server", "-loglevel", "warn"});
        assertEquals("server", info.getLogAddress());
        assertEquals("warn", info.getLogLevel());
    }
}
