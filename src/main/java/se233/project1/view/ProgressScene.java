package se233.project1.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ProgressScene extends GridPane {
    private int total, current;
    private String name;

    public ProgressScene() {
        this.setPrefWidth(640);
        this.setPrefHeight(480);
        this.start();
    }

    private void start() {
        this.setAlignment(Pos.CENTER);
        refresh();
    }

    private void refresh() {
        this.getChildren().clear();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        Label label = new Label(String.format("%s/%s %.2f%%", current, total, (double) current / total * 100));
        Label nameLabel = new Label(String.format("%s", name));
        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(320);
        bar.setProgress((double) current / total);
        vbox.setSpacing(6);
        vbox.getChildren().addAll(label, nameLabel, bar);
        this.getChildren().add(vbox);
    }

    public void setProgress(int current, int total, String name) {
        this.total = total;
        this.current = current;
        this.name = name;
        refresh();
    }
}
