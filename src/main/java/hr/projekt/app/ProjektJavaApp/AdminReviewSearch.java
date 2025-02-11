package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.Review;
import hr.projekt.app.ProjektJavaApp.Model.Reviewable;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class AdminReviewSearch {
    @FXML
    private TextField usernameTextField;
    @FXML
    private TableView<Review<Reviewable>> reviewTableView;
    @FXML
    private TableColumn<Review<Reviewable>, String> mediaTypeTableColumn;
    @FXML
    private TableColumn<Review<Reviewable>, String> usernameTableColumn;
    @FXML
    private TableColumn<Review<Reviewable>, String> mediaTableColumn;
    @FXML
    private TableColumn<Review<Reviewable>, String> commentTableColumn;

    public void initialize(){
        mediaTypeTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getMediaType().toString()));
        usernameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getUsername()));
        mediaTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getMedia().getTitle()));
        commentTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getComment()));
    }

    public void reviewSearch(){
        String username = usernameTextField.getText();

        Task<List<Review<Reviewable>>> searchTask = new Task<>() {
            @Override
            protected List<Review<Reviewable>> call() {
                return DatabaseUtils.getReviewsFiltered(username);
            }
        };

        searchTask.setOnSucceeded(event -> {
            List<Review<Reviewable>> reviewList = searchTask.getValue();
            ObservableList<Review<Reviewable>> reviewObservableList = FXCollections.observableArrayList(reviewList);
            reviewTableView.setItems(reviewObservableList);
        });

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    public void mainMenu(){
        AdminUtils.adminMainMenu();
    }
}
