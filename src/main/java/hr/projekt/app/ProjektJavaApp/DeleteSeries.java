package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Series;
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

public class DeleteSeries {
    @FXML
    private ChoiceBox<String> seriesChoiceBox;

    public void initialize(){
        List<Series> series = DatabaseUtils.getSeries();
        List<String> seriesNames = series.stream()
                .map(Series::getTitle)
                .toList();
        seriesChoiceBox.setItems(FXCollections.observableList(seriesNames));
    }

    public void deleteSeries(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to delete this series?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    String seriesString = seriesChoiceBox.getValue();
                    List<Series> seriesList = DatabaseUtils.getSeries();
                    Series series = seriesList.stream()
                            .filter(series1 -> series1.getTitle().equals(seriesString))
                            .findFirst()
                            .get();
                    DatabaseUtils.deleteSeries(series);

                    Admin admin = AdminUtils.getAdmin();
                    ChangesUtils.logChange("Series Was Deleted", seriesString, " ", admin.getUsername());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Delete was successful");
                    alert.setHeaderText("Deletion of wanted series was sucessful");
                    alert.setContentText("Series " + series.getTitle() + " was deleted!");
                    alert.showAndWait();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Saving was not successful");
                    alert.setHeaderText("You did not choose a series!");

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
        String seriesString = seriesChoiceBox.getValue();
        if(seriesString == null){
            return false;
        }else{
            return true;
        }
    }
}
