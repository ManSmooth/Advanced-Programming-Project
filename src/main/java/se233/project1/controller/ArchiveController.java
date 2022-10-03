package se233.project1.controller;

import java.util.ArrayList;
import java.util.HashMap;
import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import se233.project1.Launcher;
import se233.project1.model.ArchiveTypeWrapper;
import se233.project1.model.FileWrapper;
import se233.project1.model.TarArchiveMethodMap;
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
        String ext = ArchiveTypeWrapper.getWrapper(active).getExtension();
        if (active == ArchiveType.TAR) {
            ext = TarArchiveMethodMap.getMap().get((TarCompressionMethod) info.get("compression"));
            if ((Boolean) info.get("encrypted")) {
                info.replace("name", String.format("%s.%s", ((String) info.get("name")), ext));
                ext = "gpg";
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Destination");
        fileChooser.setInitialFileName(String.format("%s.%s", (String) info.get("name"), ext));
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Archive File",
                        String.format("*.%s", ext)));
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
                        ArchiveMaster.rar(selectedFile, info);
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

    public static void unarchive(ArrayList<FileWrapper> files) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new java.io.File("."));
        fileChooser.setTitle("Extract to");
        FileWrapper target = new FileWrapper(fileChooser.showDialog(Launcher.getPrimaryStage()));
        if (target.getFile() == null) {
            MainController.returnToHome();
            return;
        }
        setTotalFiles(files.size());
        for (FileWrapper source : files) {
            System.out.println(source.getName());
            switch (source.getExtension()) {
                case "zip":
                    ArchiveMaster.unzip(source, target);
                    break;
                case "7z":
                    ArchiveMaster.unsevenZ(source, target);
                    break;
                case "rar":
                    ArchiveMaster.unrar(source, target);
                    break;
                case "tar":
                case "tgz":
                case "tbz2":
                case "txz":
                case "tzst":
                case "gpg":
                    ArchiveMaster.untar(source, target);
                    break;
            }
        }
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

    public static void setProgress(String name) {
        updateProgress(name);
        System.out.printf("%d/%d, %s\n", finishedFiles, totalFiles, name);
    }

    public static void progress() {
        finishedFiles++;
        if (finishedFiles == totalFiles) {
            System.out.println("Finished, Returning to home.");
            MainController.returnToHome();
        }
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
}
