package se233.project1.controller;

import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class SceneController {
    private HashMap<String, Pane> sceneMap = new HashMap<>();
    private Scene main;

    public SceneController(Scene main) {
        this.main = main;
    }

    public void addScene(String name, Pane pane){
        sceneMap.put(name, pane);
    }

    public void removeScene(String name){
        sceneMap.remove(name);
    }

    public void activate(String name){
        main.setRoot( sceneMap.get(name) );
    }

    public Pane getScene(String name) {
        return sceneMap.get(name);
    }
}
