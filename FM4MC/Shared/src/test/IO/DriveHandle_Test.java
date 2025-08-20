package IO;

import IO.impl.DriveHandle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class DriveHandle_Test {

    @TempDir
    Path tempDir;

    @Test
    public void createFolderFromFile_CreatesParent() {
        File file = tempDir.resolve("sub/inner.txt").toFile();
        new DriveHandle().createFolderFromFile(file.getPath());
        assertTrue(file.getParentFile().exists());
    }

    @Test
    public void createFolderFromPath_CreatesParent() {
        Path filePath = tempDir.resolve("nested/file.txt");
        new DriveHandle().createFolderFromPath(filePath.toString());
        assertTrue(filePath.getParent().toFile().exists());
    }
}
