package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.Review;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.NamedEntity;
import hr.projekt.app.ProjektJavaApp.Model.Reviewable;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class EditReview {
    @FXML
    private ChoiceBox<Review<Reviewable>> reviewChoiceBox;
    @FXML
    private TextArea newInfoTextArea;

    public void initialize(){
        List<Review<Reviewable>> reviewList = DatabaseUtils.getReviews();
        reviewChoiceBox.setItems(FXCollections.observableList(reviewList));
    }

    public void editReview(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to edit this review?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    Review<Reviewable> reviewValue = reviewChoiceBox.getValue();
                    String newInfo = newInfoTextArea.getText();

                    DatabaseUtils.editReview(reviewValue, newInfo);

                    Admin admin = AdminUtils.getAdmin();
                    ChangesUtils.logChange("Review " +reviewValue+ " Was Edited", reviewValue.getComment(), newInfo, admin.getUsername());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Editing was successful!");
                    alert.setHeaderText("Editing of wanted review was sucessful!");
                    alert.setContentText("Review of " + reviewValue.getMedia() + " was edited!");
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
        Review<Reviewable> reviewValue = reviewChoiceBox.getValue();
        String newInfo = newInfoTextArea.getText();

        if(reviewValue == null || newInfo.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}
