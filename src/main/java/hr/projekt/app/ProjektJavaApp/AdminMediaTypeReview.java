package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Enum.MediaType;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;

public class AdminMediaTypeReview {
    @FXML
    private ChoiceBox<MediaType> mediaTypeChoiceBox;

    public void initialize(){
        mediaTypeChoiceBox.setItems(FXCollections.observableArrayList(MediaType.values()));
        mediaTypeChoiceBox.setOnAction(event -> chosenOption());
    }

    public void chosenOption(){
        MediaType mediaType = mediaTypeChoiceBox.getValue();

        if (mediaType == MediaType.MOVIE) {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(HelloApplication.class.getResource(
                            "adminAddNewReviewMovie.fxml"));
            try {
                Scene scene = new Scene(fxmlLoader.load(), 600, 600);
                HelloApplication.getStage().setTitle("Add New Movie Review");
                HelloApplication.getStage().setScene(scene);
                HelloApplication.getStage().show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mediaType == MediaType.SERIES) {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(HelloApplication.class.getResource(
                            "adminAddNewReviewSeries.fxml"));
            try {
                Scene scene = new Scene(fxmlLoader.load(), 600, 600);
                HelloApplication.getStage().setTitle("Add New Series Review");
                HelloApplication.getStage().setScene(scene);
                HelloApplication.getStage().show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }
}
