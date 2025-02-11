package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AdminAddNewGenre {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    public void saveGenre(){
        String genreName = nameTextField.getText();
        String genreDescription = descriptionTextField.getText();

        Integer id = DatabaseUtils.getNextGenreId();

        if(checkForError()){
            Genre newGenre = new Genre(id, genreName, genreDescription);

            List<Genre> genreList = new ArrayList<>();
            genreList.add(newGenre);

            DatabaseUtils.saveGenre(genreList);

            Admin admin = AdminUtils.getAdmin();
            ChangesUtils.logChange("New Genre Added", " ", newGenre.getName(), admin.getUsername());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was successful!");
            alert.setHeaderText("Genre was saved successfully!");
            alert.setContentText("Genre " + genreName + " " + " is saved!");
            alert.showAndWait();

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving was unsuccessful.");
            alert.setHeaderText("Correctly fill in the genre information!");
            alert.showAndWait();
            logger.error("Invalid genre information.");
        }
    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }

    public boolean checkForError(){
        String name = nameTextField.getText();
        String description = descriptionTextField.getText();

        if(name.isEmpty() || description.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}
