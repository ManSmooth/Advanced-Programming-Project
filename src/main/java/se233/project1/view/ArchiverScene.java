package se233.project1.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import se233.project1.Launcher;
import se233.project1.controller.ArchiveController;
import se233.project1.model.ArchiveTypeWrapper;
import se233.project1.model.PlatformFont;

public class ArchiverScene extends GridPane {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final CornerRadii CORNER = new CornerRadii(10);

    public ArchiverScene() {
        this.prefWidth(640);
        this.prefHeight(480);
        this.setVgap(12);
        this.setHgap(10);
        this.start();
    }

    public void start() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(10, 10, 10, 10));
        refresh();
    }

    public void refresh() {
        this.getChildren().clear();
        VBox container = new VBox();
        HBox hbox = new HBox();
        hbox.setSpacing(12);
        container.setSpacing(12);
        ArchiveController.getArchiveList().forEach(o -> {
            Image img = new Image(Launcher.class.getResourceAsStream(o.getIconPath()));
            ImageView icon = new ImageView(img);
            Button b = new Button();
            icon.setFitHeight(100);
            icon.setFitWidth(100);
            if (o.getType() == ArchiveController.getActive()) {
                b.setBackground(
                        new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.2), CORNER, Insets.EMPTY)));
            } else {
                b.setBackground(
                        new Background(new BackgroundFill(Color.TRANSPARENT, CORNER, Insets.EMPTY)));
                b.setOnMouseEntered(e -> b.setBackground(
                        new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), CORNER, Insets.EMPTY))));
                b.setOnMouseExited(e -> b.setBackground(
                        new Background(new BackgroundFill(Color.TRANSPARENT, CORNER, Insets.EMPTY))));
            }
            b.setGraphic(icon);
            b.setOnAction(e -> {
                ArchiveController.setArchiveType(o.getType());
                refresh();
            });
            hbox.getChildren().add(b);
        });
        Label label = new Label("Select an archive type");
        if (ArchiveController.getActive() != null) {
            label.setText(ArchiveTypeWrapper.getWrapper(ArchiveController.getActive()).getDescription());
        }
        label.setFont(PlatformFont.getFont("Roboto-Regular", 18));
        InputPane input = new InputPane(ArchiveController.getActive());
        Button submit = new Button("Submit");
        submit.setPadding(new Insets(5, 5, 5, 5));
        submit.setFont(PlatformFont.getFont("Roboto-Regular", 18));
        submit.setOnAction(e -> ArchiveController.archive(input.getFields()));
        if (ArchiveController.getActive() == null) {
            submit.setDisable(true);
        }
        container.getChildren().addAll(hbox, label, input, submit);
        container.setAlignment(Pos.CENTER);
        this.add(container, 0, 0);
    }
}
