package kitool.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MainView.fxml"));

        Scene scene = new Scene(loader.load(), 1100, 720);

        // Light Mode als Standard
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/darkMode.css")
                ).toExternalForm());

        stage.setTitle("MyKiTool");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}