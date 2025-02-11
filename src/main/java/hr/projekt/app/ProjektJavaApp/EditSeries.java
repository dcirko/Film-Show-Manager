package hr.projekt.app.ProjektJavaApp;

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
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.List;

public class EditSeries {
    @FXML
    private ChoiceBox<String> seriesChoiceBox;
    @FXML
    private ChoiceBox<String> infoChoiceBox;
    @FXML
    private TextField newInfoTextField;

    public void initialize(){
        List<Series> series = DatabaseUtils.getSeries();
        List<String> seriesNames = series.stream()
                .map(Series::getTitle)
                .toList();
        seriesChoiceBox.setItems(FXCollections.observableList(seriesNames));
        infoChoiceBox.getItems().addAll("NAME", "RATING", "RELEASE_YEAR", "NUMBER_OF_SEASONS", "ACTOR");
    }

    public void editSeries(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to edit this series?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    String chosenInfo = infoChoiceBox.getValue();
                    String newInfo = newInfoTextField.getText();
                    String seriesString = seriesChoiceBox.getValue();

                    List<Series> seriesList = DatabaseUtils.getSeries();
                    Series series = seriesList.stream()
                            .filter(series1 -> series1.getTitle().equals(seriesString))
                            .findFirst()
                            .get();

                    DatabaseUtils.editSeries(series, chosenInfo, newInfo);

                    if(chosenInfo.equals("NAME")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Series " + series + " Name Was Edited", series.getTitle(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("RATING")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Rating Of " + series + " Was Edited", series.getRating().toString(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("RELEASE_YEAR")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Release Year Of " + series + " Was Edited", series.releaseYear().toString(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("NUMBER_OF_SEASONS")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Number Of Seasons Of "+ series + " Was Edited", series.getNumberOfSeasons().toString(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("ACTOR")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Actor From " + series + " Was Edited", series.getActor().toString(), newInfo, admin.getUsername());
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Editing was successful!");
                    alert.setHeaderText("Editing of wanted series was sucessful!");
                    alert.setContentText("Series " + series.getTitle() + " was edited!");
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
        String seriesString = seriesChoiceBox.getValue();
        String chosenInfo = infoChoiceBox.getValue();
        String newInfo = newInfoTextField.getText();

        if(chosenInfo == null || seriesString == null || newInfo.isEmpty()){
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
