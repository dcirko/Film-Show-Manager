package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class GenreSearch {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TableView<Genre> genreTableView;
    @FXML
    private TableColumn<Genre, String> nameTableColumn;
    @FXML
    private TableColumn<Genre, String> descriptionTableColumn;

    public void initialize(){
        nameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        descriptionTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDescription()));
    }

    public void genreSearch(){
        String genreName = nameTextField.getText();
        String genreDescription = descriptionTextField.getText();

        /*List<Genre> genreList = DatabaseUtils.getGenreFiltered(genreName, genreDescription);
        ObservableList<Genre> genreObservableList = FXCollections.observableArrayList(genreList);
        genreTableView.setItems(genreObservableList);*/

        Task<List<Genre>> searchTask = new Task<>() {
            @Override
            protected List<Genre> call() {
                return DatabaseUtils.getGenreFiltered(genreName, genreDescription);
            }
        };

        searchTask.setOnSucceeded(event -> {
            List<Genre> genreList = searchTask.getValue();
            ObservableList<Genre> genreObservableList = FXCollections.observableArrayList(genreList);
            genreTableView.setItems(genreObservableList);
        });

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    public void mainMenu(){
        UserUtils.mainMenu();
    }

}
