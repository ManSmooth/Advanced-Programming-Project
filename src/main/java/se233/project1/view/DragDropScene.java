package se233.project1.view;

import javafx.scene.layout.GridPane;
import se233.project1.Launcher;
import se233.project1.controller.MainController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
public class DragDropScene extends GridPane {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    public DragDropScene() {
        this.setPrefWidth(WIDTH);
        this.setPrefHeight(HEIGHT);
        this.setHgap(10);
        this.setVgap(12);
        this.start();
    }

    public void start() {
        DragDropBox ddp = new DragDropBox();
        confirmedFiles();
        ddp.setOnDragOver(MainController::onDragOverHitBox);
        ddp.setOnDragDropped(e -> {
            MainController.addFileOnDrop(e);
            MainController.showConfirmationWindow();
        });
        this.setAlignment(Pos.CENTER);
        this.add(ddp, 0, 0, 1, 1);
    }

    public static void confirmedFiles() {
        ArchiverScene as = new ArchiverScene();
        Launcher.getPrimaryStage().setScene(new Scene(as));
    }
}
