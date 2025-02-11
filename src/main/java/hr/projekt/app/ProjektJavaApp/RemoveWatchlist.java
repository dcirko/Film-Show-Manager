package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.WatchlistItem;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.util.List;

public class RemoveWatchlist {
    @FXML
    private ChoiceBox<String> watchlistChoiceBox;

    public void initialize(){
        List<WatchlistItem> watchlistItemList = DatabaseUtils.getWatchlist();
        List<String> watchlistNames = watchlistItemList.stream()
                .map(WatchlistItem::getName)
                .toList();
        watchlistChoiceBox.setItems(FXCollections.observableList(watchlistNames));
    }

    public void removeFromWatchlist(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to remove this from your watchlist?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    String chosenItem = watchlistChoiceBox.getValue();
                    List<WatchlistItem> watchlistItemList = DatabaseUtils.getWatchlist();
                    WatchlistItem watchlistItem = watchlistItemList.stream()
                            .filter(watchlistItem1 -> watchlistItem1.getName().equals(chosenItem))
                            .findFirst()
                            .get();
                    DatabaseUtils.removeMediaFromWatchlist(watchlistItem);

                    UserPair<String, String> user = UserUtils.getLoggedInUser();
                    ChangesUtils.logChange("Media Removed From Watchlist", chosenItem, " ", user.getUsername());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Removal was successful");
                    alert.setHeaderText("Removal of wanted item was sucessful");
                    alert.setContentText(watchlistItem.getName() + " was removed from watchlist!");
                    alert.showAndWait();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Removal was not successful");
                    alert.setHeaderText("You did not choose a watchlist item!");

                    alert.showAndWait();
                }
            } else if (buttonType == noButton) {
            }
        });
    }

    public boolean checkForError(){
        String chosenItem = watchlistChoiceBox.getValue();
        if(chosenItem == null){
            return false;
        }else{
            return true;
        }
    }

    public void backToWatchlist(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "watchlist.fxml"));
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
