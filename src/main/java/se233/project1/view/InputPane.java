package se233.project1.view;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import se233.project1.controller.ArchiveController.ArchiveType;
import se233.project1.model.ArchiveTypeWrapper;
import se233.project1.model.PlatformFont;

import java.util.HashMap;

public class InputPane extends GridPane {
    private ArchiveType type;
    private TextField name;
    private PasswordField password;
    private ComboBox<Object> compressionBox;
    private CheckBox encryptedBox;

    public InputPane(ArchiveType type) {
        if (type != null) {
            this.type = type;
        }
        this.setAlignment(Pos.CENTER);
        this.setVgap(10);
        this.setHgap(10);
        this.start();
    }

    private void start() {
        if (this.type != null) {
            Label nameLabel = new Label("Archive Name:");
            nameLabel.setFont(PlatformFont.getFont("Roboto-Light", 18));
            name = new TextField();
            name.setFont(PlatformFont.getFont("Roboto-Light", 18));
            name.setPromptText("...name...");
            this.add(nameLabel, 0, 0);
            this.add(name, 1, 0);
            Label compressionLabel = new Label("Compression:");
            compressionLabel.setFont(PlatformFont.getFont("Roboto-Light", 18));
            compressionBox = new ComboBox<>(ArchiveTypeWrapper.getWrapper(type).getOptions());
            this.add(compressionLabel, 0, 1);
            this.add(compressionBox, 1, 1);
            encryptedBox = new CheckBox();
            password = new PasswordField();
            Label encryptedLabel = new Label("Encrypted:");
            if (ArchiveTypeWrapper.getWrapper(type).canEncrypt()) {
                encryptedLabel.setFont(PlatformFont.getFont("Roboto-Light", 18));
                password.setPromptText("...password...");
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
                encryptedBox.setOnAction(value -> {
                    if (encryptedBox.isSelected()) {
                        this.add(showPasswordBox, 1, 4);
                        this.add(textField, 1, 3);
                        this.add(password, 1, 3);
                    } else {
                        this.getChildren().remove(password);
                        this.getChildren().remove(textField);
                        this.getChildren().remove(showPasswordBox);
                        password.clear();
                    }
                });
                this.add(encryptedBox, 1, 2);
                this.add(encryptedLabel, 0, 2);
            }
        }

    }

    public HashMap<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("name", name.getText());
        fields.put("password", password.getText());
        fields.put("encrypted", encryptedBox.isSelected());
        fields.put("compression", compressionBox.getValue());
        return fields;
    }
}
