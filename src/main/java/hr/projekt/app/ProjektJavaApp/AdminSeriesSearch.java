package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Exceptions.InvalidGenreSearchException;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Series;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdminSeriesSearch {
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Genre> genreComboBox;
    @FXML
    private TableView<Series> seriesTableView;
    @FXML
    private TableColumn<Series, String> nameTableColumn;
    @FXML
    private TableColumn<Series, String> genreTableColumn;
    @FXML
    private TableColumn<Series, String> ratingTableColumn;
    @FXML
    private TableColumn<Series, String> yearTableColumn;
    @FXML
    private TableColumn<Series, String> seasonsTableColumn;
    @FXML
    private TableColumn<Series, String> actorTableColumn;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    public void initialize(){
        nameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        genreTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getSeriesGenre().toString()));
        ratingTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getRating().toString()));
        yearTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().releaseYear().toString()));
        seasonsTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getNumberOfSeasons().toString()));
        actorTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getActor().toString()));
        List<Genre> genreList = DatabaseUtils.getGenres();

        genreComboBox.setItems(FXCollections.observableList(genreList));
    }

    public void seriesSearch(){
        String seriesName = nameTextField.getText();
        Genre genre = genreComboBox.getValue();

        Integer genreId;
        if(genre != null) {
            genreId = genre.getId();
        }else{
            genreId = -1;
        }

        Task<List<Series>> searchTask = new Task<>() {
            @Override
            protected List<Series> call() {
                return DatabaseUtils.getSeriesFiltered(seriesName, genreId);
            }
        };

        searchTask.setOnSucceeded(event -> {
            List<Series> seriesList = searchTask.getValue();
            try {
                throwInvalidGenreException(seriesList);
            } catch (InvalidGenreSearchException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No series found.");
                alert.setHeaderText("No series with selected genre was found.");
                alert.showAndWait();
                logger.info("No series with selected genre was found.");
            }

            ObservableList<Series> seriesObservableList = FXCollections.observableArrayList(seriesList);
            seriesTableView.setItems(seriesObservableList);
        });

        searchTask.setOnFailed(event -> {
            Throwable exception = searchTask.getException();
            if (exception != null) {
                logger.error("Error occurred during series search: " + exception.getMessage());
            }
        });

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }
    private static void throwInvalidGenreException(List<Series> seriesList) throws InvalidGenreSearchException {
        if(seriesList.isEmpty()){
            throw new InvalidGenreSearchException("No series with selected genre was found.");
        }
    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }
}
