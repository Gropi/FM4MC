package Helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LinearFMBuilder {

    public final File FM_FILE = new File("temporaryFM.json");

    public File createLinearFM(int tasks, int alternativesPerTask) {
        var stringBuilder = new StringBuilder();

        stringBuilder.append("{\n");
        appendFeatures(stringBuilder, tasks, alternativesPerTask);
        stringBuilder.append("\t\"crossTreeConstraints\": []\n");
        stringBuilder.append("}");

        saveStringToFile(stringBuilder.toString(), FM_FILE);
        return FM_FILE;
    }

    private void appendFeatures(StringBuilder stringBuilder, int tasks, int alternativesPerTask) {
        stringBuilder.append("\t\"features\": [\n");
        for (int t = 0; t < tasks; t++) {
            appendAbstractFeature(stringBuilder, t+1, tasks);

            for (int a = 0; a < alternativesPerTask; a++) {
                var lastFeature = false;
                if (t == tasks-1 && a == alternativesPerTask-1) {
                    lastFeature = true;
                }
                appendAlternativeFeature(stringBuilder, t+1, a+1, lastFeature);
            }
        }
        stringBuilder.append("\t],\n");
    }

    private void appendAbstractFeature(StringBuilder stringBuilder, int task, int maxTasks) {
        stringBuilder.append("\t\t{\n");
        stringBuilder.append("\t\t\t\"name\": \"task").append(task).append("\",\n");
        stringBuilder.append("\t\t\t\"parentName\": \"root\",\n");
        stringBuilder.append("\t\t\t\"relation\": \"MANDATORY\",\n");
        if (task <maxTasks) {
            stringBuilder.append("\t\t\t\"reachableAbstractFeatures\": [\n");
            stringBuilder.append("\t\t\t\t\"task").append((task+1)).append("\"\n");
            stringBuilder.append("\t\t\t]\n");
        } else {
            stringBuilder.append("\t\t\t\"reachableAbstractFeatures\": []\n");
        }
        stringBuilder.append("\t\t},\n");
    }

    private void appendAlternativeFeature(StringBuilder stringBuilder, int task, int alternative, boolean lastFeature) {
        stringBuilder.append("\t\t{\n");
        stringBuilder.append("\t\t\t\"name\": \"t").append(task).append("v").append(alternative).append("\",\n");
        stringBuilder.append("\t\t\t\"parentName\": \"task").append(task).append("\",\n");
        stringBuilder.append("\t\t\t\"relation\": \"ALTERNATIVE\",\n");
        stringBuilder.append("\t\t\t\"hardwareRequirements\": []\n");
        stringBuilder.append("\t\t}");
        if (!lastFeature) {
            stringBuilder.append(",");
        }
        stringBuilder.append("\n");
    }

    private void saveStringToFile(String data, File file) {
        try {
            var bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
