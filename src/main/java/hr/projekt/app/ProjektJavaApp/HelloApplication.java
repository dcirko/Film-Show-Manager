package hr.projekt.app.ProjektJavaApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static Stage mainStage;
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("firstScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Welcome!");
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getStage(){
        return mainStage;
    }

    public static void main(String[] args) {
        launch();
    }
}