package se233.project1.controller;

import java.util.ArrayList;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import se233.project1.Launcher;
import java.io.File;

import se233.project1.view.ArchiverScene;
import se233.project1.view.FileConfPane;
import se233.project1.view.PasswordPane;
import se233.project1.model.ArchiveTypeWrapper;
import se233.project1.model.FileWrapper;
import org.apache.commons.io.FilenameUtils;

public class MainController {
    private static ArrayList<FileWrapper> filesList;

    static {
        filesList = new ArrayList<>();
    }

    public static void onDragOverHitBox(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    public static void addFileOnDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            int total_files = db.getFiles().size();
            for (int i = 0; i < total_files; i++) {
                File file = db.getFiles().get(i);
                filesList.add(new FileWrapper(file));
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    public static void showConfirmationWindow() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Launcher.getPrimaryStage());
        FileConfPane fcp = new FileConfPane();
        dialog.setOnCloseRequest(e -> filesList.clear());
        dialog.setResizable(false);
        dialog.setTitle("Confirmation");
        dialog.setScene(new Scene(fcp));
        dialog.show();
    }

    public static String showPasswordPane(FileWrapper file) {
        final PasswordPane pp = new PasswordPane();
        final Thread cThread = Thread.currentThread();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(Launcher.getPrimaryStage());
                dialog.setResizable(false);
                dialog.setTitle(String.format("Password Required: %s", file.getName()));
                dialog.setScene(new Scene(pp));
                dialog.showAndWait();
                synchronized (cThread) {
                    cThread.notify();
                }
            }
        });
        synchronized (cThread) {
            try {
                cThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return pp.getPassword();
    }

    public static void nextScene() {
        if (filesList.stream().anyMatch(
                (fw) -> !ArchiveTypeWrapper.getAllExtensions()
                        .contains(FilenameUtils.getExtension(fw.getName().toLowerCase())))) {
            Launcher.getSceneController().activate("Archiver");
        } else {
            ArchiveController.unarchive(filesList);
        }
    }

    public static ArrayList<FileWrapper> getFilesList() {
        return filesList;
    }

    public static void setFilesList(ArrayList<FileWrapper> filesList) {
        MainController.filesList = filesList;
    }

    public static void returnToHome() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Launcher.getSceneController().activate("DragDrop");
                ArchiveController.setArchiveType(null);
                ArchiverScene as = (ArchiverScene) Launcher.getSceneController().getScene("Archiver");
                as.refresh();
                MainController.getFilesList().clear();
            }
        });
    }
}
