package hr.projekt.app.ProjektJavaApp.Utils;

import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UserUtils {
    private static UserPair<String, String> loggedInUser;
    private static Set<UserPair<String, String>> userPairSet = new HashSet<>();

    public static void setLoggedInUser(UserPair<String, String> user) {
        loggedInUser = user;
        userPairSet.add(loggedInUser);
    }

    public static UserPair<String, String> getLoggedInUser() {
        return loggedInUser;
    }

    public static Set<UserPair<String, String>> getUserPairSet() {
        return userPairSet;
    }

    public static void setUserPairSet(Set<UserPair<String, String>> userPairSet) {
        UserUtils.userPairSet = userPairSet;
    }

    public static void mainMenu(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "startScreen.fxml"));
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
