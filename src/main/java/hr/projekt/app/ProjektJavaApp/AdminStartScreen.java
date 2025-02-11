package hr.projekt.app.ProjektJavaApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class AdminStartScreen {
    @FXML
    private ImageView imageView;

    public void initialize() {
        try {
            InputStream stream = getClass().getResourceAsStream("/img/movie.jpg");

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

    public void goToWatchlist(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "adminWatchlist.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getStage().setTitle("Watchlist");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
