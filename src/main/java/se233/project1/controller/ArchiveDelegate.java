package se233.project1.controller;

import se233.project1.controller.Archiver.RarArchiverController;
import se233.project1.controller.Archiver.SevenZArchiverController;
import se233.project1.controller.Archiver.TarArchiverController;
import se233.project1.controller.Archiver.ZipArchiverController;
import se233.project1.model.FileWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

import org.bouncycastle.openpgp.PGPException;

import com.github.junrar.exception.RarException;

import java.io.File;
import java.io.IOException;

public class ArchiveDelegate {
    private static ForkJoinPool pool = ForkJoinPool.commonPool();

    // Delegate Methods
    public static void zip(FileWrapper target, HashMap<String, Object> info) {
        try {
            ZipArchiverController.zipWithInfo(target, info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sevenZ(FileWrapper target, HashMap<String, Object> info) {
        try {
            SevenZArchiverController.sevenZWithInfo(target, info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void tar(FileWrapper target, HashMap<String, Object> info) {
        try {
            TarArchiverController.tarWithInfo(target, info);
        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    public static void rar(FileWrapper target, HashMap<String, Object> info) {
        try {
            RarArchiverController.rarWithInfo(target, info);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                try {
                    ZipArchiverController.unzip(source, target);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    ArchiveController.progress();
                }
            }
        });
    }

    public static void unsevenZ(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                try {
                    SevenZArchiverController.unsevenZ(source, target);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    ArchiveController.progress();
                }
            }
        });
    }

    public static void unrar(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                try {
                    RarArchiverController.unrar(source, target);
                } catch (IOException | RarException e) {
                    e.printStackTrace();
                } finally {
                    ArchiveController.progress();
                }
            }
        });
    }

    public static void untar(FileWrapper source, FileWrapper target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                ArchiveController.setProgress(source.getName());
                try {
                    TarArchiverController.untar(source, target);
                } catch (IOException | PGPException e) {
                    e.printStackTrace();
                } finally {
                    ArchiveController.progress();
                }
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
