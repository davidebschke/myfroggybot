package kitool.backend.service;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.Model;
import io.github.ollama4j.models.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class OllamaService {

    private final OllamaAPI ollamaAPI;
    private String currentModel = "llama3";

    public OllamaService() {
        this.ollamaAPI = new OllamaAPI("http://localhost:11434");
        this.ollamaAPI.setRequestTimeoutSeconds(300);
    }

    // Prüft ob Ollama läuft
    public boolean isOllamaRunning() {
        try {
            return ollamaAPI.ping();
        } catch (Exception _) {
            return false;
        }
    }

    // Gibt alle installierten Modelle zurück
    public List<String> getAvailableModels() {
        try {
           return ollamaAPI.listModels()
                    .stream()
                    .map(Model::getName)
                    .toList();

        } catch (InterruptedException | OllamaBaseException | IOException | URISyntaxException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Sendet eine Nachricht und gibt die Antwort zurück
    public String chat(String userMessage) throws Exception {
        OllamaResult result = ollamaAPI.generate(
                currentModel,
                userMessage,
                false,
                new OptionsBuilder().build()
        );
        return result.getResponse();
    }

    public String getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(String model) {
        this.currentModel = model;
    }
}
