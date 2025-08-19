package Businesslogic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility to compare file sizes of configuration outputs generated
 * with and without slicing, writing the results to a CSV file.
 */
public class StorageDifferenceCalculator {

    /**
     * Entry point that calculates and stores file size differences.
     *
     * @param args ignored command line arguments
     */
    public static void main(String[] args) {

        var outputFile = "fileSizeComparison.csv";

        var noSlicingFolderPath = "D:\\02_Ergebnisse\\Edge-Flex\\Benchmarks\\2024_05_19_Measurement_Linear_FM_No_Slicing\\ConfigurationFiles\\";
        var slicingFolderPath = "D:\\02_Ergebnisse\\Edge-Flex\\Benchmarks\\2024_05_16_Measurement_Linear_FM_Slicing\\Configuration Files\\";

        var configurationFileNameBase = "temporaryConfigurationFile";

        var slicingAppendix = "_10.csv";
        var noSlicingAppendix = "_NoSlicing.csv";

        var sb = new StringBuilder();
        sb.append("tasks;alternatives;slicingFileSize;noSlicingFileSize;factor\n");

        for (int i = 2; i < 101; i++) {
            for (int j = 1; j < 16; j++) {

                try {
                    var slicingFile = new File(slicingFolderPath + configurationFileNameBase + i + "_" + j + slicingAppendix);
                    var noSlicingFile = new File(noSlicingFolderPath + configurationFileNameBase + i + "_" + j + noSlicingAppendix);
                    if (slicingFile.exists() && noSlicingFile.exists()) {
                        var fileSizeSlicing = slicingFile.length();
                        var fileSizeNoSlicing = noSlicingFile.length();
                        sb.append(i).append(";")
                                .append(j).append(";")
                                .append(fileSizeSlicing).append(";")
                                .append(fileSizeNoSlicing).append(";")
                                .append(fileSizeNoSlicing/fileSizeSlicing).append("\n");
                    }
                } catch (NullPointerException e) {
                    System.out.println("error");
                }
            }
        }

        try {
            var br = new BufferedWriter(new FileWriter(outputFile));
            br.write(sb.toString());
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
