package hr.projekt.app.ProjektJavaApp;

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
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class EditMovie {
    @FXML
    private ChoiceBox<String> movieChoiceBox;
    @FXML
    private ChoiceBox<String> infoChoiceBox;
    @FXML
    private TextField newInfoTextField;

    public void initialize(){
        List<Movie> movies = DatabaseUtils.getMovies();
        List<String> movieNames = movies
                .stream()
                .map(Movie::getTitle)
                .toList();
        movieChoiceBox.setItems(FXCollections.observableList(movieNames));
        infoChoiceBox.getItems().addAll("NAME", "LENGTH", "RATING", "RELEASE_YEAR", "MOVIE_REVENUE", "ACTOR");
    }

    public void editMovie(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to edit this movie?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    String chosenInfo = infoChoiceBox.getValue();
                    String newInfo = newInfoTextField.getText();
                    String movieString = movieChoiceBox.getValue();

                    List<Movie> movies = DatabaseUtils.getMovies();
                    Movie movie = movies.stream()
                            .filter(movie1 -> movie1.getTitle().equals(movieString))
                            .findFirst()
                            .get();

                    DatabaseUtils.editMovie(movie, chosenInfo, newInfo);

                    if(chosenInfo.equals("NAME")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Movie " + movie + " Name Was Edited", movie.getTitle(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("LENGTH")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Length Of " + movie +" Was Edited", movie.returnMovieLength().toString(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("RATING")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Rating Of " + movie + " Was Edited", movie.getRating().toString(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("RELEASE_YEAR")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Release Year Of " + movie + " Was Edited", movie.releaseYear().toString(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("MOVIE_REVENUE")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Movie Revenue Of " + movie + " Was Edited", movie.getMovieRevenue().toString(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("ACTOR")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Actor From " + movie + " Was Edited", movie.getActor().toString(), newInfo, admin.getUsername());
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Editing was successful!");
                    alert.setHeaderText("Editing of wanted movie was sucessful!");
                    alert.setContentText("Movie " + movie.getTitle() + " was edited!");
                    alert.showAndWait();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Editing was not successful!");
                    alert.setHeaderText("Correctly input all information!");

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
        String chosenInfo = infoChoiceBox.getValue();
        String newInfo = newInfoTextField.getText();

        if(chosenInfo == null || movieString == null || newInfo.isEmpty()){
            return false;
        }else if(chosenInfo.equals("RATING")){
            BigDecimal rating = new BigDecimal(newInfo);
            if(rating.compareTo(BigDecimal.valueOf(10)) > 0 || rating.compareTo(BigDecimal.valueOf(0)) < 0){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }
}
