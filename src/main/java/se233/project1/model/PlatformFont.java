package se233.project1.model;

import javafx.scene.text.Font;
import se233.project1.Launcher;

public class PlatformFont {
    public static Font getFont(String key, double size) {
        return Font.loadFont(Launcher.class.getResourceAsStream(String.format("assets/fonts/%s.ttf", key)), size);
    }
}
