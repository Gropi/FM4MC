package Helper;

import java.lang.management.ManagementFactory;

public class ProgramHelper {
    // Debug flag (set to false in production)
    public static final boolean DEBUG = isDebuggerAttached();

    private static boolean isDebuggerAttached() {
        var inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (var arg : inputArgs) {
            if (arg.contains("-agentlib:jdwp")) {
                return true;
            }
        }
        return false;
    }
}
