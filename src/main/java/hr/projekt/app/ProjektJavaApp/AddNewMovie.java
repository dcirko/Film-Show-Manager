package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.*;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddNewMovie {
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Genre> genreComboBox;
    @FXML
    private TextField lengthTextField;
    @FXML
    private TextField ratingTextField;
    @FXML
    private TextField yearTextField;
    @FXML
    private TextField revenueTextField;
    @FXML
    private TextField actorTextField;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    public void initialize(){
        List<Genre> genreList = DatabaseUtils.getGenres();

        genreComboBox.setItems(FXCollections.observableList(genreList));
    }

    public void saveMovie(){
        String movieName = nameTextField.getText();
        Genre genre = genreComboBox.getValue();
        String length = lengthTextField.getText();
        String rating = ratingTextField.getText();
        String year = yearTextField.getText();
        String revenue = revenueTextField.getText();
        String actor = actorTextField.getText();

        if(checkForError()){
            Integer movieLength = Integer.valueOf(length);
            BigDecimal movieRating = new BigDecimal(rating);
            Integer releaseYear = Integer.valueOf(year);
            BigDecimal movieRevenue = new BigDecimal(revenue);
            Revenue finRevenue = new Revenue(movieRevenue);
            String[] names = actor.split(" ");
            String actorName = names[0];
            String actorSurname = names[1];
            Actor actor1 = new Actor.Builder().name(actorName).surname(actorSurname).build();
            Integer movieId = DatabaseUtils.getNextMovieId();

            Movie newMovie = new Movie(movieId, movieName, movieLength, genre, movieRating, releaseYear, finRevenue, actor1);

            List<Movie> movieList = new ArrayList<>();

            movieList.add(newMovie);


            DatabaseUtils.saveMovie(movieList);

            UserPair<String, String> user = UserUtils.getLoggedInUser();
            ChangesUtils.logChange("New Movie Added", " ", newMovie.getTitle(), user.getUsername());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was successful!");
            alert.setHeaderText("Movie was saved successfully!");
            alert.setContentText("Movie " + movieName + " " + " is saved!");
            alert.showAndWait();

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was unsuccessful.");
            alert.setHeaderText("Correctly fill in the movie information!");
            alert.showAndWait();
            logger.error("Invalid movie information.");
        }
    }

    public void mainMenu(){
        UserUtils.mainMenu();
    }

    public boolean checkForError(){
        String movieName = nameTextField.getText();
        Genre genre = genreComboBox.getValue();
        String length = lengthTextField.getText();
        String rating = ratingTextField.getText();
        String year = yearTextField.getText();
        String revenue = revenueTextField.getText();
        String actor = actorTextField.getText();

        if(movieName.isEmpty() || genre == null || length.isEmpty() || rating.isEmpty() || year.isEmpty()
                || revenue.isEmpty() || actor.isEmpty() || rating.compareTo("10") > 0 || rating.compareTo("0") < 0) {
            return false;
        }else{
            return true;
        }
    }
}
