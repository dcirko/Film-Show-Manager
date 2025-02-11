package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.NamedEntity;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteMovie {
    @FXML
    private ChoiceBox<String> movieChoiceBox;

    public void initialize(){
        List<Movie> movies = DatabaseUtils.getMovies();
        List<String> movieNames = movies
                .stream()
                .map(Movie::getTitle)
                .toList();
        movieChoiceBox.setItems(FXCollections.observableList(movieNames));
    }

    public void deleteMovie(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to delete this movie?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    String movieString = movieChoiceBox.getValue();
                    List<Movie> movies = DatabaseUtils.getMovies();
                    Movie movie = movies.stream()
                            .filter(movie1 -> movie1.getTitle().equals(movieString))
                            .findFirst()
                            .get();
                    DatabaseUtils.deleteMovies(movie);

                    Admin admin = AdminUtils.getAdmin();
                    ChangesUtils.logChange("Movie Was Deleted", movieString, " ", admin.getUsername());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Delete was successful");
                    alert.setHeaderText("Deletion of wanted movie was sucessful");
                    alert.setContentText("Movie " + movie.getTitle() + " was deleted!");
                    alert.showAndWait();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Saving was not successful");
                    alert.setHeaderText("You did not choose a movie!");

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
        String movieString = movieChoiceBox.getValue();
        if(movieString == null){
            return false;
        }else{
            return true;
        }
    }
}
