package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.Actor;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.Series;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
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
import java.util.ArrayList;
import java.util.List;

public class AdminAddNewSeries {
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Genre> genreComboBox;
    @FXML
    private TextField ratingTextField;
    @FXML
    private TextField yearTextField;
    @FXML
    private TextField seasonsTextField;
    @FXML
    private TextField actorTextField;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    public void initialize(){
        List<Genre> genreList = DatabaseUtils.getGenres();

        genreComboBox.setItems(FXCollections.observableList(genreList));
    }

    public void saveSeries(){
        String seriesName = nameTextField.getText();
        Genre genre = genreComboBox.getValue();
        String rating = ratingTextField.getText();
        String year = yearTextField.getText();
        String seasons = seasonsTextField.getText();
        String actor = actorTextField.getText();

        if(checkForError()){
            BigDecimal seriesRating = new BigDecimal(rating);
            Integer releaseYear = Integer.valueOf(year);
            Integer numOfSeasons = Integer.valueOf(seasons);
            Integer seriesId = DatabaseUtils.getNextSeriesId();
            String[] names = actor.split(" ");
            String actorName = names[0];
            String actorSurname = names[1];
            Actor actor1 = new Actor.Builder().name(actorName).surname(actorSurname).build();

            Series newSeries = new Series(seriesId, seriesName, genre, seriesRating, releaseYear, numOfSeasons, actor1);

            List<Series> seriesList = new ArrayList<>();
            seriesList.add(newSeries);

            DatabaseUtils.saveSeries(seriesList);

            Admin admin = AdminUtils.getAdmin();
            ChangesUtils.logChange("New Series Added", " ", newSeries.getTitle(), admin.getUsername());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was successful!");
            alert.setHeaderText("Series was saved successfully!");
            alert.setContentText("Series " + seriesName + " " + " is saved!");
            alert.showAndWait();

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was unsuccessful.");
            alert.setHeaderText("Correctly fill in the series information!");
            alert.showAndWait();
            logger.error("Invalid series information.");
        }
    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }

    public boolean checkForError(){
        String seriesName = nameTextField.getText();
        Genre genre = genreComboBox.getValue();
        String rating = ratingTextField.getText();
        String year = yearTextField.getText();
        String seasons = seasonsTextField.getText();
        String actor = actorTextField.getText();

        if(seriesName.isEmpty() || genre == null || rating.isEmpty() || rating.compareTo("10") > 0 || year.isEmpty()
                || seasons.isEmpty() || actor.isEmpty() ||
                rating.compareTo("0") < 0){
            return false;
        }else{
            return true;
        }
    }
}
