package se233.project1.controller;

import se233.project1.controller.Archiver.SevenZArchiverController;
import se233.project1.controller.Archiver.TarArchiverController;
import se233.project1.controller.Archiver.ZipArchiverController;
import se233.project1.model.FileWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

public class ArchiveMaster {

    public static void zip(FileWrapper target, HashMap<String, Object> info) {
        ZipArchiverController.zipWithInfo(target, info);
    }

    public static void sevenZ(FileWrapper target, HashMap<String, Object> info) {
        SevenZArchiverController.sevenZWithInfo(target, info);
    }

    public static void tar(FileWrapper target, HashMap<String, Object> info) {
        TarArchiverController.tarWithInfo(target, info);
    }

    public static HashMap<FileWrapper, String> flattenFileToHashMap(ArrayList<FileWrapper> filelist, String base) {
        HashMap<FileWrapper, String> fileMap = new HashMap<>();
        for (FileWrapper fileWrapper : filelist) {
            if (fileWrapper.getFile().isFile()) {
                fileMap.put(fileWrapper, base);
            } else {
                ArrayList<FileWrapper> files = new ArrayList<>();
                for (File file : fileWrapper.getFile().listFiles()) {
                    files.add(new FileWrapper(file));
                }
                if (!files.isEmpty()) {
                    fileMap.putAll(flattenFileToHashMap(files, base + fileWrapper.getName() + "/"));
                }
            }
        }
        return fileMap;
    }
}
