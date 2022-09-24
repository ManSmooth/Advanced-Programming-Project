package se233.project1.view;

import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import se233.project1.Launcher;
import se233.project1.model.PlatformFont;

public class DragDropBox extends VBox {
    public DragDropBox() {
        this.setPrefWidth(DragDropScene.WIDTH);
        this.setPrefHeight(DragDropScene.HEIGHT);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(12);
        this.start();
    }

    private void start() {
        Image img = new Image(Launcher.class.getResourceAsStream("assets/drop.png"));
        ImageView icon = new ImageView(img);
        Label label = new Label("Drag files(s) here");
        label.setFont(PlatformFont.getFont("Roboto-Medium", 24));
        label.setOpacity(0.5);
        icon.setOpacity(0.5);
        this.getChildren().addAll(icon, label);
    }
}
