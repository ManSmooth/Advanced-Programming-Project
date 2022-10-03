package se233.project1.controller;

import java.util.HashMap;

import com.jfoenix.controls.JFXDecorator;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import se233.project1.Launcher;

public class SceneController {
    private HashMap<String, Pane> decoratorMap = new HashMap<>();
    private HashMap<String, Pane> sceneMap = new HashMap<>();
    private Scene main;

    public SceneController(Scene main) {
        this.main = main;
    }

    public void addScene(String name, Pane pane) {
        JFXDecorator decorator = new JFXDecorator(Launcher.getPrimaryStage(), pane, false, false, true);
        decorator.setText("Archiver v000");
        decorator.setOnCloseButtonAction(() -> Platform.exit());
        sceneMap.put(name, pane);
        decoratorMap.put(name, decorator);
    }

    public void removeScene(String name) {
        sceneMap.remove(name);
        decoratorMap.remove(name);
    }

    public void activate(String name) {
        main.setRoot(decoratorMap.get(name));
    }

    public Pane getScene(String name) {
        return sceneMap.get(name);
    }
}
