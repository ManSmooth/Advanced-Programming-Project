package se233.project1.model;

import java.io.File;

public class FileWrapper {
    private String name;
    private String path;
    private File file;

    public FileWrapper(File file) {
        if (file == null) {
            this.name = null;
            this.path = null;
        } else {
            this.name = file.getName();
            this.path = file.getAbsolutePath();
        }
        this.file = file;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }
}
