package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.NamedEntity;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.List;

public class EditGenre {
    @FXML
    private ChoiceBox<String> genreChoiceBox;
    @FXML
    private ChoiceBox<String> infoChoiceBox;
    @FXML
    private TextField newInfoTextField;

    public void initialize(){
        List<Genre> genreList = DatabaseUtils.getGenres();
        List<String> genres = genreList
                .stream()
                .map(NamedEntity::getName)
                .toList();
        genreChoiceBox.setItems(FXCollections.observableList(genres));
        infoChoiceBox.getItems().addAll("NAME", "DESCRIPTION");
    }

    public void editGenre(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to edit this genre?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    String genreString = genreChoiceBox.getValue();
                    String chosenInfo = infoChoiceBox.getValue();
                    String newInfo = newInfoTextField.getText();

                    List<Genre> genreList = DatabaseUtils.getGenres();
                    Genre genre = genreList
                            .stream()
                            .filter(genre1 -> genre1.getName().equals(genreString))
                            .findFirst()
                            .get();

                    DatabaseUtils.editGenre(genre, chosenInfo, newInfo);

                    if(chosenInfo.equals("NAME")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Genre " + genre + " Was Edited", genre.getName(), newInfo, admin.getUsername());
                    }else if(chosenInfo.equals("DESCRIPTION")){
                        Admin admin = AdminUtils.getAdmin();
                        ChangesUtils.logChange("Genre " + genre + " Was Edited", genre.getDescription(), newInfo, admin.getUsername());
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Editing was successful!");
                    alert.setHeaderText("Editing of wanted genre was sucessful!");
                    alert.setContentText("Genre " + genre.getName() + " was edited!");
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
        String genreString = genreChoiceBox.getValue();
        String chosenInfo = infoChoiceBox.getValue();
        String newInfo = newInfoTextField.getText();

        if(genreString == null || chosenInfo == null || newInfo.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}

