package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Enum.MediaType;
import hr.projekt.app.ProjektJavaApp.Exceptions.InvalidReviewException;
import hr.projekt.app.ProjektJavaApp.Genericsi.Review;
import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Reviewable;
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

public class AdminAddNewReviewMovie {
    @FXML
    private ComboBox<String> movieComboBox;
    @FXML
    private TextArea commentTextArea;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    public void initialize(){
        List<Movie> movieList = DatabaseUtils.getMovies();
        List<String> movieNames = movieList
                .stream()
                .map(Movie::getTitle)
                .toList();
        movieComboBox.setItems(FXCollections.observableList(movieNames));
    }

    public void saveReview(){
        String movieName = movieComboBox.getValue();
        String comment = commentTextArea.getText();

        try{
            throwInvalidReviewException(movieName, comment);
            List<Movie> movieList = DatabaseUtils.getMovies();
            Movie movie = movieList
                    .stream()
                    .filter(movie1 -> movie1.getTitle().equals(movieName))
                    .findFirst()
                    .get();

            Admin admin = AdminUtils.getAdmin();
            MediaType mediaType = MediaType.MOVIE;
            Integer id = DatabaseUtils.getNextReviewId();

            Review<Reviewable> review = new Review<>(id,mediaType, admin.getUsername(), movie, comment);

            List<Review<Reviewable>> reviewList = new ArrayList<>();
            reviewList.add(review);

            DatabaseUtils.saveReviews(reviewList);

            ChangesUtils.logChange("New Movie Review Added", " ", review.toString(), admin.getUsername());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was successful!");
            alert.setHeaderText("Review was saved successfully!");
            alert.setContentText("Review of " + movie.getTitle() + " " + " is saved!");
            alert.showAndWait();
        }catch (InvalidReviewException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was unsuccessful.");
            alert.setHeaderText("Correctly fill in the review information!");
            alert.showAndWait();
            logger.error("Invalid input for review. Choose a movie and leave a comment.");
        }
    }

    private static void throwInvalidReviewException(String movieName, String comment) {
        if(movieName == null || comment.isEmpty()){
            throw new InvalidReviewException("Invalid input for review. Choose a movie and leave a comment.");
        }
    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }
}
