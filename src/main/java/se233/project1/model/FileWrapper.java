package se233.project1.model;

import java.io.File;
import org.apache.commons.compress.utils.FileNameUtils;

public class FileWrapper {
    private String name;
    private String path;
    private String extension;
    private File file;

    public FileWrapper(File file) {
        if (file == null) {
            this.name = null;
            this.path = null;
            this.extension = null;
        } else {
            this.name = file.getName();
            this.path = file.getAbsolutePath();
            this.extension = FileNameUtils.getExtension(file.getName());
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

    public String getExtension() {
        return extension;
    }

    public File getFile() {
        return file;
    }
}
