package logging;

import Monitoring.Event.Logging.ILogInformation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class MyLogManager_Test {

    private static class StubLogInfo implements ILogInformation {
        @Override
        public String getLogAddress() { return "/tmp"; }
        @Override
        public String getLogLevel() { return "TRACE"; }
    }

    @Test
    public void start_SetsRootLevel() {
        var manager = new MyLogManager(new StubLogInfo());
        manager.start();

        Logger root = (Logger) LogManager.getRootLogger();
        assertEquals(Level.TRACE, root.getLevel());
    }

    @Test
    public void createCompletePath_BuildsPath() throws Exception {
        Method m = MyLogManager.class.getDeclaredMethod("createCompletePath", String[].class);
        m.setAccessible(true);
        String[] parts = {"a", "b"};
        String expected = File.separator + "a" + File.separator + "b";
        assertEquals(expected, m.invoke(null, (Object) parts));
    }
}
