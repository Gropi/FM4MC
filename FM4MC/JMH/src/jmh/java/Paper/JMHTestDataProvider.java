package Paper;

import java.util.Arrays;
import java.util.Collections;

public class JMHTestDataProvider {
    public String[] getTestFilesWithPath() {
        var files = new String[5];
        files[0] = "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json";
        files[1] = "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json";
        files[2] = "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json";
        files[3] = "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json";
        files[4] = "../TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json";
        return files;
    }

    public String[] getTestFilesWithPathInverted() {
        var files = getTestFilesWithPath();
        Collections.reverse(Arrays.asList(files));
        return files;
    }

    /**
     * Gibt ein Array von Dateipfaden zurück, die den angegebenen Indizes entsprechen.
     *
     * @param indices Array der gewünschten Indizes
     * @return Array der Dateipfade
     * @throws IllegalArgumentException wenn ein Index außerhalb des gültigen Bereichs liegt
     */
    public String[] getTestFilesWithPath(int[] indices) {
        var files = getTestFilesWithPath();
        // Überprüfen, ob die Indizes gültig sind
        for (int index : indices) {
            if (index < 0 || index >= files.length) {
                throw new IllegalArgumentException("Ungültiger Index: " + index);
            }
        }

        // Erstellen des Ergebnisarrays basierend auf den angegebenen Indizes
        String[] selectedFiles = new String[indices.length];
        for (int i = 0; i < indices.length; i++) {
            selectedFiles[i] = files[indices[i]];
        }
        return selectedFiles;
    }

    public String[] getThresholds() {
        return new String[] {"3", "4", "5", "6", "7", "8", "9", "10"};
    }

    public String[] getMaxRequirements() {
        return new String[] {"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    }

    public String[] getMaxRequirementsInverted() {
        var requirements = getMaxRequirements();
        Collections.reverse(Arrays.asList(requirements));
        return requirements;
    }

    public String[] getEdgeIDs() {
        return new String[] {"5", "4", "3", "2", "1"};
    }

    public String[] getEdgeIDsInverted() {
        var ids = getEdgeIDs();
        Collections.reverse(Arrays.asList(ids));
        return ids;
    }

    public String getTestFilesWithPathAsString() {
        var filesAsString = new StringBuilder();
        var files = getTestFilesWithPath();
        for (int i = 0; i < files.length; i++) {
            filesAsString.append(files[i]);
            if (i < files.length - 1) {
                filesAsString.append(",");
            }
        }
        return filesAsString.toString();
    }
}
