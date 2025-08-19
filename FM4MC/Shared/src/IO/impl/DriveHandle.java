package IO.impl;

import java.io.File;

public class DriveHandle {
    public void createFolderFromFile(String fileWithPath) throws IllegalStateException {
        var file = new File(fileWithPath);
        createFolderFromFile(file);
    }

    public void createFolderFromPath(String path) throws IllegalStateException {
        if (!path.endsWith(String.valueOf(File.separatorChar))) {
            path = path + File.separatorChar;
        }
        createFolderFromFile(new File(path));
    }

    public void createFolderFromFile(File file) throws IllegalStateException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
    }
}
