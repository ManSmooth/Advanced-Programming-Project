package se233.project1.controller;

import java.util.ArrayList;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import se233.project1.Launcher;
import java.io.File;
import se233.project1.view.FileConfPane;
import se233.project1.model.FileWrapper;

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

    public static void showArchiverScene() {
        Launcher.getSceneController().activate("Archiver");
    }

    public static ArrayList<FileWrapper> getFilesList() {
        return filesList;
    }

    public static void setFilesList(ArrayList<FileWrapper> filesList) {
        MainController.filesList = filesList;
    }
}
