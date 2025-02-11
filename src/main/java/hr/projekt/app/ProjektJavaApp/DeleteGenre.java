package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.Review;
import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.NamedEntity;
import hr.projekt.app.ProjektJavaApp.Model.Reviewable;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteGenre {
    @FXML
    private ChoiceBox<String> genreChoiceBox;

    public void initialize(){
        List<Genre> genreList = DatabaseUtils.getGenres();
        List<String> genres = genreList
                .stream()
                .map(NamedEntity::getName)
                .toList();
        genreChoiceBox.setItems(FXCollections.observableList(genres));
    }

    public void deleteGenre(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to delete this genre?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    String genreString = genreChoiceBox.getValue();
                    List<Genre> genreList = DatabaseUtils.getGenres();
                    Genre genre = genreList
                            .stream()
                            .filter(genre1 -> genre1.getName().equals(genreString))
                            .findFirst()
                            .get();

                    DatabaseUtils.deleteGenres(genre);

                    Admin admin = AdminUtils.getAdmin();
                    ChangesUtils.logChange("Genre Was Deleted", genreString, " ", admin.getUsername());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Delete was successful");
                    alert.setHeaderText("Deletion of wanted genre was sucessful");
                    alert.setContentText("Genre " + genre.getName() + " was deleted!");
                    alert.showAndWait();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Saving was not successful");
                    alert.setHeaderText("You did not choose a genre!");

                    alert.showAndWait();
                }
            } else if (buttonType == noButton) {
            }
        });

    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }

    public boolean checkForError(){
        String genreString = genreChoiceBox.getValue();
        if(genreString == null){
            return false;
        }else{
            return true;
        }
    }
}
