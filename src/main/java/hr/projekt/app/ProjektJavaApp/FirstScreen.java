package hr.projekt.app.ProjektJavaApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class FirstScreen {
    @FXML
    private ImageView imageView;

    public void initialize() {
        try {
            InputStream stream = getClass().getResourceAsStream("/img/first.jpg");

            if (stream != null) {
                Image image = new Image(stream);
                imageView.setImage(image);
                double desiredWidth = 600;
                double desiredHeight = 600;
                imageView.setFitWidth(desiredWidth);
                imageView.setFitHeight(desiredHeight);
            } else {
                System.out.println("Image file not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userLogin(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "userLogin.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getStage().setTitle("Login As User");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adminLogin(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "adminLogin.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getStage().setTitle("Login As Administrator");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}