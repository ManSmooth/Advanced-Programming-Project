package se233.project1.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import se233.project1.Launcher;
import se233.project1.model.ArchiveTypeWrapper;
import se233.project1.model.FileWrapper;
import se233.project1.view.ArchiverScene;
import se233.project1.view.ProgressScene;

public class ArchiveController {
    public enum ArchiveType {
        ZIP, SEVENZIP, RAR, TAR
    }

    private static int totalFiles, finishedFiles;
    private static ArchiveType active = null;
    private static ArrayList<ArchiveTypeWrapper> archiveList = new ArrayList<>();
    private static ProgressScene ps;
    private static long start = 0;
    static {
        archiveList.add(new ArchiveTypeWrapper(ArchiveType.ZIP));
        archiveList.add(new ArchiveTypeWrapper(ArchiveType.SEVENZIP));
        archiveList.add(new ArchiveTypeWrapper(ArchiveType.RAR));
        archiveList.add(new ArchiveTypeWrapper(ArchiveType.TAR));
    }

    public static void archive(HashMap<String, Object> info) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Destination");
        fileChooser.setInitialFileName(
                String.format("%s.%s", info.get("name"), ArchiveTypeWrapper.getWrapper(active).getExtension()));
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Archive Files",
                        String.format("*.%s", ArchiveTypeWrapper.getWrapper(active).getExtension())));
        FileWrapper selectedFile = new FileWrapper(fileChooser.showSaveDialog(Launcher.getPrimaryStage()));
        if (selectedFile.getFile() == null) {
            return;
        }
        new Thread() {
            public void run() {
                switch (active) {
                    case ZIP:
                        ArchiveMaster.zip(selectedFile, info);
                        break;
                    case SEVENZIP:
                        ArchiveMaster.sevenZ(selectedFile, info);
                        break;
                    case RAR:
                        break;
                    case TAR:
                        ArchiveMaster.tar(selectedFile, info);
                        break;
                }
                Thread.currentThread().interrupt();
            }
        }.start();
        ps = (ProgressScene) Launcher.getSceneController().getScene("Progress");
        Launcher.getSceneController().activate("Progress");
    }

    public static ArchiveType getActive() {
        return active;
    }

    public static void setArchiveType(ArchiveType type) {
        active = type;
    }

    public static ArrayList<ArchiveTypeWrapper> getArchiveList() {
        return archiveList;
    }

    public static void setTotalFiles(int _totalFiles) {
        totalFiles = _totalFiles;
        finishedFiles = 0;
        updateProgress("");
    }

    public static void progress(String name) {
        finishedFiles++;
        updateProgress(name);
    }

    public static void updateProgress(String name) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ps.setProgress(finishedFiles, totalFiles, name);
            }
        });
    }

    public static void time() {
        if (start == 0l)
            start = System.nanoTime();
        else {
            long end = System.nanoTime();
            System.out.printf("Operation Completed, Elapsed: %.4fs\n", (end - start) / 1000000000.0);
            start = 0l;
        }
    }

    public static void returnToHome() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Launcher.getSceneController().activate("DragDrop");
                ArchiverScene as = (ArchiverScene) Launcher.getSceneController().getScene("Archiver");
                as.refresh();
                MainController.getFilesList().clear();
                active = null;
            }
        });
    }
}
