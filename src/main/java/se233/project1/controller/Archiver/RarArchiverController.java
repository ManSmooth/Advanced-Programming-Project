package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

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
            ArrayList<String> params = new ArrayList<>();
            params.add("C:/Program Files/WinRAR/rar");
            params.add("a");
            params.add("-ep1");
            params.add("-o+");
            params.add("-r");
            params.add(String.format("-m%c", ((String) info.get("compression")).charAt(0)));
            if ((boolean) info.get("encrypted")) {
                params.add(String.format("-p%s", (String) info.get("password")));
            }
            params.add(target.getFile().getAbsolutePath());
            params.add(String.format("@%s", f.getAbsolutePath()));
            ProcessBuilder pb = new ProcessBuilder(params);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                for (String line = null; (line = reader.readLine()) != null;) {
                    System.err.println(line);
                }
            }
            System.out.println("Rar finished with exit code:" + p.waitFor());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArchiveController.time();
    }

    public static void unrar(FileWrapper source, FileWrapper target) {
        String pass = MainController.showPasswordPane(source);
        try (FileInputStream fis = new FileInputStream(source.getFile());) {
            if (pass.isEmpty()) {
                Junrar.extract(fis, target.getFile());
            } else {
                Junrar.extract(fis, target.getFile(), pass);
            }
        } catch (IOException | RarException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }
}
