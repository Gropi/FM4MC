package IO;

import IO.impl.ManagingMeasurement;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.LogEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagingMeasurement_Test {

    private static class RecordingAppender extends AbstractAppender {
        List<String> messages = new ArrayList<>();
        protected RecordingAppender() {
            super("record", null, PatternLayout.createDefaultLayout(), false, null);
        }
        @Override
        public void append(LogEvent event) {
            messages.add(event.getMessage().getFormattedMessage());
        }
    }

    @Test
    public void writeLine_JoinsWithSeparator() {
        Logger logger = (Logger) LogManager.getLogger("measurementTest");
        logger.setLevel(Level.INFO);
        RecordingAppender appender = new RecordingAppender();
        appender.start();
        logger.addAppender(appender);

        var mm = new ManagingMeasurement("measurementTest", ",");
        mm.writeLine("a", "b");

        assertEquals("a,b,", appender.messages.get(0));
        logger.removeAppender(appender);
    }
}
