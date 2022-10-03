package se233.project1.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import se233.project1.model.PlatformFont;

public class PasswordPane extends GridPane {
    private PasswordField password;

    public PasswordPane() {
        this.setHgap(10);
        this.setVgap(5);
        this.setPrefWidth(320);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.getStyleClass().add("container");
        this.start();
    }

    public void start() {
        password = new PasswordField();
        Label enterLabel = new Label("Enter Password");
        enterLabel.setFont(PlatformFont.getFont("Roboto-Light", 12));
        password.setPromptText("Leave blank if none");
        TextField textField = new TextField();
        textField.setManaged(false);
        textField.setVisible(false);
        textField.textProperty().bindBidirectional(password.textProperty());
        HBox showPasswordBox = new HBox();
        showPasswordBox.setSpacing(12);
        showPasswordBox.setAlignment(Pos.CENTER);
        Label showPasswordLabel = new Label("Show Password:");
        showPasswordLabel.setFont(PlatformFont.getFont("Roboto-Light", 12));
        CheckBox showPassword = new CheckBox();
        textField.managedProperty().bind(showPassword.selectedProperty());
        textField.visibleProperty().bind(showPassword.selectedProperty());
        password.managedProperty().bind(showPassword.selectedProperty().not());
        password.visibleProperty().bind(showPassword.selectedProperty().not());
        showPasswordBox.getChildren().addAll(showPasswordLabel, showPassword);
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            Node p = (Node) e.getSource();
            Stage s = (Stage) p.getScene().getWindow();
            s.close();
        });
        this.setAlignment(Pos.CENTER);
        this.add(enterLabel, 0, 1, 1, 1);
        this.add(textField, 0, 2, 2, 1);
        this.add(password, 0, 2, 2, 1);
        this.add(showPasswordBox, 0, 3, 2, 1);
        this.add(okButton, 1, 4, 2, 1);
    }

    public String getPassword() {
        return password.getText();
    }

}
