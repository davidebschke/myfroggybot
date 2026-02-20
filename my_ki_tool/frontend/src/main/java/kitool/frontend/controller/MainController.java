package kitool.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

import java.util.Objects;

public class MainController {
    @FXML
    private ToggleButton darkModeToggle;
    @FXML
    private Button newChatButton;
    @FXML
    private TextArea messageInput;
    @FXML
    private Button sendButton;
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

        // Darkmode Control : Wird noch ausgelagert
        darkModeToggle.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            if (Boolean.TRUE.equals(isSelected)) {
                darkModeToggle.setStyle("-fx-background-color: #5B8DEF; -fx-text-fill: #FFFFFF;");
            } else {
                darkModeToggle.setStyle("-fx-background-color: #1E2A45; -fx-text-fill: #9AA3BF;");
            }
        });

        // new Chat button pressed : Wird noch ausgelagert

        newChatButton.pressedProperty().addListener((obs, oldVal, isPressed) -> {
            if (Boolean.TRUE.equals(isPressed)) {
                newChatButton.setStyle("-fx-background-color: #4A7ADB;-fx-scale-x: 0.95;\n" +
                        "                                                -fx-scale-y: 0.95;");
            } else {
                newChatButton.setStyle("-fx-background-color: #5B8DEF;");
            }
        });


        // message input (Texteingabebereich ) : Wird noch auslagert
        messageInput.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (Boolean.TRUE.equals(isFocused)) {
                messageInput.setStyle("-fx-border-color: #5B8DEF;");
            } else {
                messageInput.setStyle("-fx-border-color: #2E2E48;");
            }
        });

        sendButton.pressedProperty().addListener((obs, oldVal, isPressed) -> {
            if (Boolean.TRUE.equals(isPressed)) {
                sendButton.setStyle("-fx-background-color: #4A7ADB;-fx-scale-x: 0.95;\n" +
                        "    -fx-scale-y: 0.95;");
            } else {
                sendButton.setStyle("-fx-background-color: #5B8DEF;-fx-scale-x: 0;\n" +
                        "    -fx-scale-y: 0;");
            }
        });

    }
}
