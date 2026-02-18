package kitool.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;

import java.util.Objects;

public class MainController {
    @FXML
    private ToggleButton darkModeToggle;
    @FXML
    private void toggleDarkMode() {
        Scene scene = darkModeToggle.getScene();
        scene.getStylesheets().clear();
        if (darkModeToggle.isSelected()) {
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/css/darkMode.css")).toExternalForm());
        } else {
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/css/lightMode.css")).toExternalForm());
        }
    }
}
