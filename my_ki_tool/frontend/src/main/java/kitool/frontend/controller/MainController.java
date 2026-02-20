package kitool.frontend.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kitool.backend.service.OllamaService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    private Label ollamaStatusLabel;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox chatMessageContainer;
    @FXML
    private ComboBox<String> modelSelector;
    @FXML
    private ListView<String> chatHistoryList;
    @FXML
    private HBox titleBar;

    private final OllamaService ollamaService = new OllamaService();


    public void initialize(URL url, ResourceBundle resourceBundle) {
        prüfeOllamaStatus();
        ladeModelle();
        konfiguriereDarkModeToggle();
        konfiguriereSendenMitEnter();
    }

    // ─────────────────────────────────────────────
    // INITIALISIERUNG
    // ─────────────────────────────────────────────

    private void prüfeOllamaStatus() {
        new Thread(() -> {
            boolean läuft = ollamaService.isOllamaRunning();
            Platform.runLater(() -> {
                if (läuft) {
                    ollamaStatusLabel.setText("● Ollama verbunden");
                    ollamaStatusLabel.getStyleClass().setAll("status-label-ok");
                } else {
                    ollamaStatusLabel.setText("● Ollama nicht erreichbar");
                    ollamaStatusLabel.getStyleClass().setAll("status-label-error");
                }
            });
        }).start();
    }

    private void ladeModelle() {
        new Thread(() -> {
            List<String> modelle = ollamaService.getAvailableModels();
            Platform.runLater(() -> {
                if (!modelle.isEmpty()) {
                    modelSelector.setItems(FXCollections.observableArrayList(modelle));
                    modelSelector.setValue(modelle.get(0));
                    ollamaService.setCurrentModel(modelle.get(0));
                } else {
                    modelSelector.setItems(FXCollections.observableArrayList("llama3"));
                    modelSelector.setValue("llama3");
                }
            });
        }).start();
        // Modellwechsel
        modelSelector.setOnAction(e -> {
            String gewähltesModell = modelSelector.getValue();
            if (gewähltesModell != null) {
                ollamaService.setCurrentModel(gewähltesModell);
            }
        });
    }


        // Darkmode Control : Wird noch ausgelagert
        private void konfiguriereDarkModeToggle() {
            darkModeToggle.selectedProperty().addListener((obs, oldVal, isSelected) -> {
                if (Boolean.TRUE.equals(isSelected)) {
                    darkModeToggle.setStyle("-fx-background-color: #5B8DEF; -fx-text-fill: #FFFFFF;-fx-border-color: #5B8DEF;");
                } else {
                    darkModeToggle.setStyle("-fx-background-color: #1E2A45; -fx-text-fill: #9AA3BF;");
                }
                toggleDarkMode();
            });
        }

    private void konfiguriereSendenMitEnter() {
        messageInput.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> {
                    if (event.isShiftDown()) {
                        // Shift+Enter = neue Zeile
                    } else {
                        event.consume();
                        sendeNachricht();
                    }
                }
            }
        });
    }

    // ─────────────────────────────────────────────
    // AKTIONEN
    // ─────────────────────────────────────────────

    @FXML
    private void sendMessage() {
        sendeNachricht();
    }

    private void sendeNachricht() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        // Willkommensbox entfernen beim ersten Chat
        chatMessageContainer.getChildren().removeIf(
                node -> node.getStyleClass().contains("welcome-box")
        );

        // User-Nachricht anzeigen
        zeigeNachricht(text, true);
        messageInput.clear();
        sendButton.setDisable(true);

        // KI-Antwort in eigenem Thread holen
        new Thread(() -> {
            try {
                String antwort = ollamaService.chat(text);
                Platform.runLater(() -> {
                    zeigeNachricht(antwort, false);
                    sendButton.setDisable(false);
                    scrolleZumEnde();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    zeigeNachricht("Fehler: " + e.getMessage(), false);
                    sendButton.setDisable(false);
                });
            }
        }).start();

        scrolleZumEnde();
    }

    private void zeigeNachricht(String text, boolean istNutzer) {
        VBox bubble = new VBox(4);

        Label senderLabel = new Label(istNutzer ? "Du" : "KI");
        senderLabel.getStyleClass().add("bubble-sender-label");

        Label nachrichtLabel = new Label(text);
        nachrichtLabel.setWrapText(true);
        nachrichtLabel.setMaxWidth(600);
        nachrichtLabel.getStyleClass().add(istNutzer ? "bubble-user-text" : "bubble-ai-text");

        bubble.getChildren().addAll(senderLabel, nachrichtLabel);
        bubble.getStyleClass().add(istNutzer ? "bubble-user" : "bubble-ai");
        bubble.setMaxWidth(650);

        HBox zeile = new HBox(bubble);
        zeile.setPadding(new Insets(4, 0, 4, 0));
        zeile.setAlignment(istNutzer ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        chatMessageContainer.getChildren().add(zeile);
    }

    private void scrolleZumEnde() {
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    @FXML
    private void newChat() {
        chatMessageContainer.getChildren().clear();

        VBox willkommen = new VBox(10);
        willkommen.getStyleClass().add("welcome-box");
        willkommen.setAlignment(Pos.CENTER);

        Label emoji = new Label("🤖");
        emoji.setStyle("-fx-font-size: 48px;");
        Label titel = new Label("Wie kann ich dir helfen?");
        titel.getStyleClass().add("welcome-title");
        Label untertitel = new Label("Stelle mir eine Frage oder starte ein Gespräch.");
        untertitel.getStyleClass().add("welcome-subtitle");

        willkommen.getChildren().addAll(emoji, titel, untertitel);
        chatMessageContainer.getChildren().add(willkommen);

        chatHistoryList.getItems().add("Chat " + (chatHistoryList.getItems().size() + 1));
    }

    @FXML
    private void toggleDarkMode() {
        javafx.scene.Scene scene = darkModeToggle.getScene();
        if (scene == null) return;
        scene.getStylesheets().clear();
        if (darkModeToggle.isSelected()) {
            scene.getStylesheets().add(
                    getClass().getResource("/css/darkMode.css").toExternalForm());
        } else {
            scene.getStylesheets().add(
                    getClass().getResource("/css/lightMode.css").toExternalForm());
        }
    }

    @FXML
    private void openSettings() {
        // Einstellungen Dialog – kommt später
        System.out.println("Einstellungen öffnen");
    }


    // new Chat button pressed : Wird noch ausgelagert
    @FXML
            private void pressedNewChat() {
        newChatButton.pressedProperty().addListener((obs, oldVal, isPressed) -> {
            if (Boolean.TRUE.equals(isPressed)) {
                newChatButton.setStyle("-fx-background-color: #4A7ADB;-fx-scale-x: 0.95;\n" +
                        "                                                -fx-scale-y: 0.95;");
            } else {
                newChatButton.setStyle("-fx-background-color: #5B8DEF;");
            }
        });
    }

    @FXML
        // message input (Texteingabebereich ) : Wird noch auslagert
    private void messageInputFocused() {
        messageInput.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (Boolean.TRUE.equals(isFocused)) {
                messageInput.setStyle("-fx-border-color: #5B8DEF;");
            } else {
                messageInput.setStyle("-fx-border-color: #2E2E48;");
            }
        });
    }

    @FXML
    private void sendButtonPressed(){
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
