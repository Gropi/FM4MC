package Console;

import Events.IConsoleInputListener;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadSafeConsoleHandler_Test {

    private static class RecordingListener implements IConsoleInputListener {
        List<String> messages = new ArrayList<>();
        @Override
        public void HandleConsoleInput(String message) {
            messages.add(message);
        }
    }

    @Test
    public void executeEvent_NotifiesListeners() throws Exception {
        var handler = new ThreadSafeConsoleHandler();
        var listener = new RecordingListener();
        handler.addListener(listener);

        Method m = ThreadSafeConsoleHandler.class.getDeclaredMethod("executeEvent", String.class);
        m.setAccessible(true);
        m.invoke(handler, "hello");

        assertEquals(List.of("hello"), listener.messages);
    }

    @Test
    public void removeListener_StopsNotification() throws Exception {
        var handler = new ThreadSafeConsoleHandler();
        var listener = new RecordingListener();
        handler.addListener(listener);
        handler.removeListener(listener);

        Method m = ThreadSafeConsoleHandler.class.getDeclaredMethod("executeEvent", String.class);
        m.setAccessible(true);
        m.invoke(handler, "ignored");

        assertTrue(listener.messages.isEmpty());
    }

    @Test
    public void stopConsoleHandler_SetsFlag() throws Exception {
        var handler = new ThreadSafeConsoleHandler();
        handler.StopConsoleHandler();

        Field f = ThreadSafeConsoleHandler.class.getDeclaredField("_StopConsole");
        f.setAccessible(true);
        assertTrue(f.getBoolean(handler));
    }
}
