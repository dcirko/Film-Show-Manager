module hr.projekt.app.projektjavaaplikacija {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;
    requires jbcrypt;

    opens hr.projekt.app.ProjektJavaApp to javafx.fxml;
    exports hr.projekt.app.ProjektJavaApp;
}