package se233.project1.controller.Archiver;

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
import org.apache.commons.io.IOUtils;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

public class RarArchiverController {

    public static void rarWithInfo(FileWrapper target, HashMap<String, Object> info)
            throws IOException, InterruptedException {
        ArrayList<FileWrapper> fileList = MainController.getFilesList();
        File f = File.createTempFile("rartext", ".tmp");
        FileOutputStream fos = new FileOutputStream(f);
        for (FileWrapper fileWrapper : fileList) {
            fos.write((fileWrapper.getFile().getAbsolutePath() + "\n").getBytes());
        }
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        for (String line = null; (line = reader.readLine()) != null;) {
            System.err.println(line);
        }
        System.out.println("Rar finished with exit code:" + p.waitFor());
        IOUtils.close(fos);
    }

    public static void unrar(FileWrapper source, FileWrapper target) throws IOException, RarException {
        String pass = MainController.showPasswordPane(source);
        FileInputStream fis = new FileInputStream(source.getFile());
        if (pass.isEmpty()) {
            Junrar.extract(fis, target.getFile());
        } else {
            Junrar.extract(fis, target.getFile(), pass);
        }

    }
}
