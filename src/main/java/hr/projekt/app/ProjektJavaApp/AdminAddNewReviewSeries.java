package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Enum.MediaType;
import hr.projekt.app.ProjektJavaApp.Exceptions.InvalidReviewException;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AdminAddNewReviewSeries {
    @FXML
    private ComboBox<String> seriesComboBox;
    @FXML
    private TextArea commentTextArea;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    public void initialize(){
        List<Series> seriesList = DatabaseUtils.getSeries();
        List<String> seriesNames = seriesList
                .stream()
                .map(Series::getTitle)
                .toList();
        seriesComboBox.setItems(FXCollections.observableList(seriesNames));
    }

    public void saveReview(){
        String seriesName = seriesComboBox.getValue();
        String comment = commentTextArea.getText();

        try{
            throwInvalidReviewException(seriesName, comment);
            List<Series> seriesList = DatabaseUtils.getSeries();
            Series series = seriesList
                    .stream()
                    .filter(series1 -> series1.getTitle().equals(seriesName))
                    .findFirst()
                    .get();
            Admin admin = AdminUtils.getAdmin();
            MediaType mediaType = MediaType.SERIES;
            Integer id = DatabaseUtils.getNextReviewId();

            Review<Reviewable> review = new Review<>(id,mediaType, admin.getUsername(), series, comment);

            List<Review<Reviewable>> reviewList = new ArrayList<>();
            reviewList.add(review);

            DatabaseUtils.saveReviews(reviewList);

            ChangesUtils.logChange("New Series Review Added", " ", review.toString(), admin.getUsername());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was successful!");
            alert.setHeaderText("Review was saved successfully!");
            alert.setContentText("Review of " + series.getTitle() + " " + " is saved!");
            alert.showAndWait();
        }catch (InvalidReviewException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was unsuccessful");
            alert.setHeaderText("Correctly fill in the review information!");
            alert.showAndWait();
            logger.error("Invalid input for review. Choose a series and leave a comment.");
        }
    }

    private static void throwInvalidReviewException(String seriesName, String comment) {
        if(seriesName == null || comment.isEmpty()){
            throw new InvalidReviewException("Invalid input for review. Choose a series and leave a comment.");
        }
    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }
}
