package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.ArchiveDelegate;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.utils.IOUtils;

import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SevenZArchiverController {
    private static SevenZOutputFile archive;

    public static void sevenZWithInfo(FileWrapper target, HashMap<String, Object> info) throws IOException {
        archive = new SevenZOutputFile(target.getFile());
        archive.setContentCompression((SevenZMethod) info.get("compression"));
        HashMap<FileWrapper, String> fileMap = ArchiveDelegate.flattenFileToHashMap(MainController.getFilesList(),
                "");
        ArchiveController.setTotalFiles(fileMap.size());
        for (FileWrapper fw : fileMap.keySet()) {
            ArchiveController.setProgress(fw.getName());
            addFileToSevenZFile(archive, fw.getFile(), fileMap.get(fw));
            ArchiveController.progress();
        }
        archive.close();
    }

    private static void addFileToSevenZFile(SevenZOutputFile sevenZOutputFile, File fileToZip, String base)
            throws IOException {
        String entryName = base + fileToZip.getName();
        SevenZArchiveEntry entry = sevenZOutputFile.createArchiveEntry(fileToZip, entryName);
        sevenZOutputFile.putArchiveEntry(entry);
        FileInputStream fileInputStream = new FileInputStream(fileToZip);
        sevenZOutputFile.write(fileInputStream);
        sevenZOutputFile.closeArchiveEntry();
        IOUtils.closeQuietly(fileInputStream);
    }

    public static void unsevenZ(FileWrapper source, FileWrapper target) throws IOException {
        SevenZFile sevenZFile;
        String pass = MainController.showPasswordPane(source);
        if (pass.isEmpty()) {
            sevenZFile = new SevenZFile(source.getFile());
        } else {
            sevenZFile = new SevenZFile(source.getFile(), pass.toCharArray());
        }
        SevenZArchiveEntry entry;
        FileOutputStream fos = null;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            ;
            File file = new File(target.getFile(), entry.getName());
            if (entry.isDirectory()) {
                file.mkdirs();
            } else {
                file.createNewFile();
                fos = new FileOutputStream(file);
                IOUtils.copy(sevenZFile.getInputStream(entry), fos);
                IOUtils.closeQuietly(fos);
            }
        }
        sevenZFile.close();
    }
}
