package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.Review;
import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Reviewable;
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

public class DeleteReview {
    @FXML
    private ChoiceBox<Review<Reviewable>> reviewChoiceBox;

    public void initialize(){
        List<Review<Reviewable>> reviewList = DatabaseUtils.getReviews();
        reviewChoiceBox.setItems(FXCollections.observableList(reviewList));
    }

    public void deleteReview(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setContentText("Are you sure you want to delete this review?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                if(checkForError()){
                    Review<Reviewable> review = reviewChoiceBox.getValue();
                    DatabaseUtils.deleteReview(review);

                    Admin admin = AdminUtils.getAdmin();
                    ChangesUtils.logChange("Review Was Deleted", review.toString(), " ", admin.getUsername());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Delete was successful");
                    alert.setHeaderText("Deletion of wanted review was sucessful");
                    alert.setContentText("Review by " + review.getUsername() + " was deleted!");
                    alert.showAndWait();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Saving was not successful");
                    alert.setHeaderText("You did not choose a review!");

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
        Review<Reviewable> review = reviewChoiceBox.getValue();
        if(review == null){
            return false;
        }else{
            return true;
        }
    }
}
