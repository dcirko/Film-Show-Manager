package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Series;
import hr.projekt.app.ProjektJavaApp.Model.WatchlistItem;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminAddWatchlistItem {
    @FXML
    private ChoiceBox<String> movieChoiceBox;
    @FXML
    private ChoiceBox<String> seriesChoiceBox;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    public void initialize(){
        List<Movie> movieList = DatabaseUtils.getMovies();
        List<String> movieNames = movieList
                .stream()
                .map(Movie::getTitle)
                .toList();
        movieChoiceBox.setItems(FXCollections.observableList(movieNames));

        List<Series> series = DatabaseUtils.getSeries();
        List<String> seriesNames = series.stream()
                .map(Series::getTitle)
                .toList();
        seriesChoiceBox.setItems(FXCollections.observableList(seriesNames));
    }

    public void addMovieToWatchlist(){
        String movieString = movieChoiceBox.getValue();
        Integer id = DatabaseUtils.getNextWatchlistitemId();
        List<WatchlistItem> watchlistItems = DatabaseUtils.getWatchlist();
        List<WatchlistItem> watchlistItemList = new ArrayList<>();
        WatchlistItem watchlistItem = new WatchlistItem(id, "MOVIE", movieString);

        if(containsWatchlistItem(watchlistItems, watchlistItem) && movieString != null){
            watchlistItemList.add(watchlistItem);
            DatabaseUtils.saveWatchlistitem(watchlistItemList);
            Admin admin = AdminUtils.getAdmin();
            ChangesUtils.logChange("New Movie Added To Watchlist", " ", watchlistItem.getName(), admin.getUsername());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was successful!");
            alert.setHeaderText("You have successfully saved chosen movie to your watchlist.");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was unsuccessful!");
            alert.setHeaderText("You have chosen a movie that is already in your watchlist or you didn't choose a movie.");
            alert.showAndWait();
            logger.error("You have chosen a movie that is already in your watchlist.");
        }
    }

    public void addSeriesToWatchlist(){
        String seriesString = seriesChoiceBox.getValue();
        Integer id = DatabaseUtils.getNextWatchlistitemId();
        List<WatchlistItem> watchlistItems = DatabaseUtils.getWatchlist();
        List<WatchlistItem> watchlistItemList = new ArrayList<>();
        WatchlistItem watchlistItem = new WatchlistItem(id, "SERIES", seriesString);

        if(containsWatchlistItem(watchlistItems, watchlistItem) && seriesString != null){
            watchlistItemList.add(watchlistItem);
            DatabaseUtils.saveWatchlistitem(watchlistItemList);

            Admin admin = AdminUtils.getAdmin();
            ChangesUtils.logChange("New Series Added To Watchlist", " ", watchlistItem.getName(), admin.getUsername());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was successful!");
            alert.setHeaderText("You have successfully saved chosen series to your watchlist.");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was unsuccessful!");
            alert.setHeaderText("You have chosen a series that is already in your watchlist or you didn't choose a series.");
            alert.showAndWait();
            logger.error("You have chosen a movie that is already in your watchlist.");
        }
    }

    public boolean containsWatchlistItem(List<WatchlistItem> watchlistItemList, WatchlistItem itemToCheck) {
        for (WatchlistItem watchlistItem : watchlistItemList) {
            if (watchlistItem.getName().equals(itemToCheck.getName())) {
                return false;
            }
        }
        return true;
    }

    public void backToWatchlist(){
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
