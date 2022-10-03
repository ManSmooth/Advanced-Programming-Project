package se233.project1;

import java.security.Security;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.project1.controller.SceneController;
import se233.project1.view.ArchiverScene;
import se233.project1.view.DragDropScene;
import se233.project1.view.ProgressScene;

public class Launcher extends Application {
    private static Stage primaryStage;
    private static SceneController sc;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage _primaryStage) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        primaryStage = _primaryStage;
        DragDropScene dds = new DragDropScene();
        ArchiverScene as = new ArchiverScene();
        ProgressScene ps = new ProgressScene();
        Scene scene = new Scene(dds);
        scene.getStylesheets().add(this.getClass().getResource("assets/style.css").toExternalForm());
        sc = new SceneController(scene);
        sc.addScene("DragDrop", dds);
        sc.addScene("Archiver", as);
        sc.addScene("Progress", ps);
        primaryStage.setResizable(false);;
        primaryStage.setScene(scene);
        sc.activate("DragDrop");
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static SceneController getSceneController() {
        return sc;
    }
}
