package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.ArchiveMaster;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.utils.IOUtils;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SevenZArchiverController {
    private static SevenZOutputFile archive;

    public static void sevenZWithInfo(FileWrapper target, HashMap<String, Object> info) {
        ArchiveController.time();
        try {
            archive = new SevenZOutputFile(target.getFile());
            archive.setContentCompression((SevenZMethod) info.get("compression"));
            HashMap<FileWrapper, String> fileMap = ArchiveMaster.flattenFileToHashMap(MainController.getFilesList(), "");
            ArchiveController.setTotalFiles(fileMap.size());
            for (FileWrapper fw : fileMap.keySet()) {
                ArchiveController.progress(fw.getName());
                addFileToSevenZFile(archive, fw.getFile(), fileMap.get(fw));
            }
            archive.close();
            ArchiveController.returnToHome();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArchiveController.time();
    }

    private static void addFileToSevenZFile(SevenZOutputFile sevenZOutputFile, File fileToZip,
            String base)
            throws IOException {
        String entryName = base + fileToZip.getName();
        SevenZArchiveEntry entry = sevenZOutputFile.createArchiveEntry(fileToZip, entryName);
        sevenZOutputFile.putArchiveEntry(entry);
        try (FileInputStream fileInputStream = new FileInputStream(fileToZip)) {
            sevenZOutputFile.write(fileInputStream);
            sevenZOutputFile.closeArchiveEntry();
            IOUtils.closeQuietly(fileInputStream);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
