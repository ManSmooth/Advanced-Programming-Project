package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RarArchiverController {

    public static void rarWithInfo(FileWrapper target, HashMap<String, Object> info) {
        ArchiveController.time();
        ArrayList<FileWrapper> fileList = MainController.getFilesList();
        File f = null;
        try {
            f = File.createTempFile("rartext", ".tmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream fos = new FileOutputStream(f)) {
            for (FileWrapper fileWrapper : fileList) {
                fos.write((fileWrapper.getFile().getAbsolutePath() + "\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Runtime.getRuntime().exec(
                    new String[] { String.format("rar a %s @%s", target.getFile().getAbsolutePath(), f.getAbsolutePath()) },
                    new String[] {},
                    new File("C:/Program Files/WinRAR"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
