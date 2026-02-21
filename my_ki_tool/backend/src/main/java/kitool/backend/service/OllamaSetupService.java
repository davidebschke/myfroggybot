package kitool.backend.service;

import java.io.*;
import java.nio.file.*;

public class OllamaSetupService {

    private static final String OLLAMA_EXE = "C:\\Users\\" +
            System.getProperty("user.name") +
            "\\AppData\\Local\\Programs\\Ollama\\ollama.exe";

    // Prüft ob Ollama bereits installiert ist
    public boolean isOllamaInstalled() {
        return new File(OLLAMA_EXE).exists();
    }

    // Extrahiert den Installer aus den Resources und führt ihn aus
    public void installiereOllama() throws Exception {
        // Installer aus Resources in temporären Ordner kopieren
        Path tempInstaller = Files.createTempFile("OllamaSetup", ".exe");

        try (InputStream is = getClass()
                .getResourceAsStream("/ollama/OllamaSetup.exe")) {

            if (is == null) {
                throw new FileNotFoundException("OllamaSetup.exe nicht in Resources gefunden!");
            }

            Files.copy(is, tempInstaller, StandardCopyOption.REPLACE_EXISTING);
        }

        // Installer ausführen und warten bis er fertig ist
        Process process = new ProcessBuilder(tempInstaller.toString())
                .redirectErrorStream(true)
                .start();

        int exitCode = process.waitFor();

        // Temporäre Datei löschen
        Files.deleteIfExists(tempInstaller);

        if (exitCode != 0) {
            throw new RuntimeException("Ollama Installation fehlgeschlagen mit Code: " + exitCode);
        }
    }

    // Startet Ollama im Hintergrund
    public void starteOllama() throws Exception {
        // Prüfen ob Ollama bereits läuft
        try {
            new java.net.Socket("localhost", 11434).close();
            return; // Läuft bereits
        } catch (Exception ignored) {}

        // Ollama starten
        new ProcessBuilder("ollama", "serve")
                .redirectErrorStream(true)
                .start();

        // Warten bis Ollama bereit ist (max 10 Sekunden)
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            try {
                new java.net.Socket("localhost", 11434).close();
                return; // Ollama ist bereit
            } catch (Exception ignored) {}
        }

        throw new RuntimeException("Ollama konnte nicht gestartet werden.");
    }
}
