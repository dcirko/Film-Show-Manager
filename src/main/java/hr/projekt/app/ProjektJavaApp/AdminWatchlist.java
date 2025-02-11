package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.WatchlistItem;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.List;

public class AdminWatchlist {
    @FXML
    private ListView<WatchlistItem> watchlistListView;
    @FXML
    private ChoiceBox<String> typeChoiceBox;


    public void initialize() {
        typeChoiceBox.getItems().addAll("MOVIE", "SERIES");
    }

    public void searchWatchlist(){
        String type = typeChoiceBox.getValue();
        List<WatchlistItem> watchlistItemList;

        if(type == null){
            watchlistItemList = DatabaseUtils.getWatchlist();
        }else{
            watchlistItemList = DatabaseUtils.getWatchlistFiltered(type);
        }

        ObservableList<WatchlistItem> watchlistItemObservableList = FXCollections.observableArrayList(watchlistItemList);
        watchlistListView.setItems(watchlistItemObservableList);

    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }

    public void addToWatchlist(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "adminAddWatchlistItem.fxml"));
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
                        "adminRemoveWatchlist.fxml"));
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
