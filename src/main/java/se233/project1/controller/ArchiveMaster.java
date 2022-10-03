package se233.project1.controller;

import se233.project1.controller.Archiver.RarArchiverController;
import se233.project1.controller.Archiver.SevenZArchiverController;
import se233.project1.controller.Archiver.TarArchiverController;
import se233.project1.controller.Archiver.ZipArchiverController;
import se233.project1.model.FileWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;
import java.io.File;

public class ArchiveMaster {
    private static ForkJoinPool pool = ForkJoinPool.commonPool();

    // Delegate Methods
    public static void zip(FileWrapper target, HashMap<String, Object> info) {
        ZipArchiverController.zipWithInfo(target, info);
    }

    public static void sevenZ(FileWrapper target, HashMap<String, Object> info) {
        SevenZArchiverController.sevenZWithInfo(target, info);
    }

    public static void tar(FileWrapper target, HashMap<String, Object> info) {
        TarArchiverController.tarWithInfo(target, info);
    }

    public static void rar(FileWrapper target, HashMap<String, Object> info) {
        RarArchiverController.rarWithInfo(target, info);
    }

    public static void unzip(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                ZipArchiverController.unzip(source, target);
                ArchiveController.progress();
            }
        });
    }

    public static void unsevenZ(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                SevenZArchiverController.unsevenZ(source, target);
                ArchiveController.progress();
            }
        });
    }

    public static void unrar(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                RarArchiverController.unrar(source, target);
                ArchiveController.progress();
            }
        });
    }

    public static void untar(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                TarArchiverController.untar(source, target);
                ArchiveController.progress();
            }
        });
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
