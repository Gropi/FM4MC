package IO;

import IO.impl.AccessDrive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccessDrive_Test {

    @TempDir
    Path tempDir;

    @Test
    public void listFilesUsingFilesList_ReturnsFiles() throws Exception {
        Path file1 = Files.createFile(tempDir.resolve("a.txt"));
        List<String> files = new AccessDrive().listFilesUsingFilesList(tempDir.toString());
        assertEquals(List.of(file1.toString()), files);
    }

    @Test
    public void writeImageToDisk_CreatesFile() throws Exception {
        BufferedImage img = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
        File out = tempDir.resolve("img/one.png").toFile();
        new AccessDrive().writeImageToDisk(img, out, "png");
        assertTrue(out.exists());
    }

    @Test
    public void getContentFromFileAsString_ReadsBase64() throws Exception {
        Path file = Files.createFile(tempDir.resolve("data.bin"));
        Files.writeString(file, "abc");
        String encoded = new AccessDrive().getContentFromFileAsString(file.toString());
        assertEquals(java.util.Base64.getEncoder().encodeToString("abc".getBytes()), encoded);
    }
}
