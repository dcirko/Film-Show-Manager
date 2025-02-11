package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Model.ChangeLog;
import hr.projekt.app.ProjektJavaApp.Utils.ChangesUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdminChangesLog {
    @FXML
    private TableView<ChangeLog> changeLogTableView;
    @FXML
    private TableColumn<ChangeLog, String> dataTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> oldValueTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> newValueTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> usernameTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> dateTimeStringTableColumn;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");

    public void initialize(){
        dataTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getChangedData()));
        oldValueTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getOldValue()));
        newValueTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getNewValue()));
        usernameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getRole()));
        dateTimeStringTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDateTime()));

        List<ChangeLog> changesList = ChangesUtils.deserializationOfChanges();

        changesList.sort(Comparator.comparing((ChangeLog cl) -> LocalDateTime.parse(cl.getDateTime(), DATE_TIME_FORMATTER)).reversed());

        ObservableList<ChangeLog> changeLogObservableList = FXCollections.observableArrayList(changesList);
        changeLogTableView.setItems(changeLogObservableList);
    }
}
