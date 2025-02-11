package hr.projekt.app.ProjektJavaApp.Utils;

import hr.projekt.app.ProjektJavaApp.HelloApplication;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class AdminUtils {
    private static Admin adminInfo;

    public static Admin getAdmin() {
        return adminInfo;
    }

    public static void setAdmin(Admin admin) {
        adminInfo = admin;
    }

    public static void adminMainMenu(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "adminStartScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getStage().setTitle("Start");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
