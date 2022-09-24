package se233.project1.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;
import javafx.scene.input.KeyCode;
import javafx.scene.control.SelectionModel;
import javafx.scene.Node;

public class FileConfPane extends GridPane {
    private ListView<FileWrapper> fileView = new ListView<>();

    public FileConfPane() {
        this.setHgap(10);
        this.setVgap(12);
        this.start();
    }

    public void start() {
        VBox container = new VBox();
        fileView.getItems().addAll(MainController.getFilesList());
        fileView.setOnKeyPressed(arg0 -> {
            if (arg0.getCode() == KeyCode.DELETE) {
                SelectionModel<FileWrapper> selection = fileView.getSelectionModel();
                if (!selection.isEmpty()) {
                    MainController.getFilesList().remove(selection.getSelectedItem());
                    selection.clearSelection();
                    updateFileView();
                }
            }
        });
        fileView.setOnDragOver(MainController::onDragOverHitBox);
        fileView.setOnDragDropped(e -> {
            MainController.addFileOnDrop(e);
            updateFileView();
        });
        Button contButton = new Button("Proceed");
        contButton.setOnAction(e -> {
            Node p = (Node) e.getSource();
            Stage s = (Stage) p.getScene().getWindow();
            s.close();
            MainController.showArchiverScene();
        });
        contButton.setPadding(new Insets(5, 5, 5, 5));
        container.setPadding(new Insets(5, 5, 5, 5));
        container.setAlignment(Pos.CENTER);
        container.setSpacing(5);
        container.getChildren().addAll(fileView, contButton);
        this.setAlignment(Pos.CENTER);
        this.add(container, 0, 0, 1, 1);
    }

    private void updateFileView() {
        fileView.getItems().clear();
        fileView.getItems().addAll(MainController.getFilesList());
    }
}
