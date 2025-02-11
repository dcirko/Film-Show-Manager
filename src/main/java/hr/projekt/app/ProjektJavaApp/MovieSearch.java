package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Exceptions.InvalidGenreSearchException;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MovieSearch {
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Genre> genreComboBox;
    @FXML
    private TableView<Movie> movieTableView;
    @FXML
    private TableColumn<Movie, String> nameTableColumn;
    @FXML
    private TableColumn<Movie, String> genreTableColumn;
    @FXML
    private TableColumn<Movie, String> lengthTableColumn;
    @FXML
    private TableColumn<Movie, String> ratingTableColumn;
    @FXML
    private TableColumn<Movie, String> yearTableColumn;
    @FXML
    private TableColumn<Movie, String> revenueTableColumn;
    @FXML
    private TableColumn<Movie, String> actorTableColumn;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    public void initialize(){
        nameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        genreTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getMovieGenre().toString()));
        lengthTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().returnMovieLength().toString()));
        ratingTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getRating().toString()));
        yearTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().releaseYear().toString()));
        revenueTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getMovieRevenue().toString()));
        actorTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getActor().toString()));

        List<Genre> genreList = DatabaseUtils.getGenres();

        genreComboBox.setItems(FXCollections.observableList(genreList));
    }

    public void movieSearch() {
        String movieName = nameTextField.getText();
        Genre genre = genreComboBox.getValue();

        Integer genreId;
        if(genre != null) {
            genreId = genre.getId();
        }else{
            genreId = -1;
        }

        Task<List<Movie>> searchTask = new Task<>() {
            @Override
            protected List<Movie> call() {
                return DatabaseUtils.getMovieFiltered(movieName, genreId);
            }
        };

        searchTask.setOnSucceeded(event -> {
            List<Movie> movieList = searchTask.getValue();
            try {
                throwInvalidGenreException(movieList);
            } catch (InvalidGenreSearchException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No movies found.");
                alert.setHeaderText("No movie with selected genre was found.");
                alert.showAndWait();
                logger.info("No movie with selected genre was found.");
            }

            ObservableList<Movie> movieObservableList = FXCollections.observableArrayList(movieList);
            movieTableView.setItems(movieObservableList);
        });

        searchTask.setOnFailed(event -> {
            Throwable exception = searchTask.getException();
            if (exception != null) {
                logger.error("Error occurred during movie search: " + exception.getMessage());
            }
        });

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private static void throwInvalidGenreException(List<Movie> movieList) throws InvalidGenreSearchException {
        if(movieList.isEmpty()){
            throw new InvalidGenreSearchException("No movie with selected genre was found.");
        }
    }

    public void mainMenu(){
        UserUtils.mainMenu();
    }
}
