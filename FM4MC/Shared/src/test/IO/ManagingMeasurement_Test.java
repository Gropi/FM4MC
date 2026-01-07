package IO;

import IO.impl.ManagingMeasurement;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
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
        var appender = new RecordingAppender();
        appender.start();

        var context = (LoggerContext) LogManager.getContext(false);

        var config = context.getConfiguration();

        var loggerName = "measurementTest";

        var loggerConfig = config.getLoggerConfig(loggerName);

        if (!loggerConfig.getName().equals(loggerName)) {
            loggerConfig =
                    new LoggerConfig(loggerName, Level.INFO, false);
            config.addLogger(loggerName, loggerConfig);
        }

        loggerConfig.addAppender(appender, Level.INFO, null);
        context.updateLoggers();

        var mm = new ManagingMeasurement(loggerName, ",");

        mm.writeLine("a", "b");

        assertEquals(1, appender.messages.size());
        assertEquals("a,b,", appender.messages.get(0));

        loggerConfig.removeAppender("record");
    }
}
