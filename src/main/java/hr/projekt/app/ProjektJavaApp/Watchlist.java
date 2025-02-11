package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Series;
import hr.projekt.app.ProjektJavaApp.Model.WatchlistItem;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Watchlist {
    @FXML
    private ListView<WatchlistItem> watchlistListView;
    @FXML
    private ChoiceBox<String> typeChoiceBox;


    public void initialize() {
        typeChoiceBox.getItems().addAll("MOVIE", "SERIES");
    }

    public void searchWatchlist(){
        String type = typeChoiceBox.getValue();

        Task<List<WatchlistItem>> searchWatchlistTask = new Task<>() {
            @Override
            protected List<WatchlistItem> call() {
                if (type == null) {
                    return DatabaseUtils.getWatchlist();
                } else {
                    return DatabaseUtils.getWatchlistFiltered(type);
                }
            }
        };

        searchWatchlistTask.setOnSucceeded(event -> {
            List<WatchlistItem> watchlistItemList = searchWatchlistTask.getValue();
            ObservableList<WatchlistItem> watchlistItemObservableList = FXCollections.observableArrayList(watchlistItemList);
            watchlistListView.setItems(watchlistItemObservableList);
        });

        Thread watchlistThread = new Thread(searchWatchlistTask);
        watchlistThread.setDaemon(true);
        watchlistThread.start();

    }

    public void mainMenu(){
        UserUtils.mainMenu();
    }

    public void addToWatchlist(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "addWatchlistItem.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getStage().setTitle("Add To Watchlist");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFromWatchlist(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "removeWatchlist.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getStage().setTitle("Remove From Watchlist");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
