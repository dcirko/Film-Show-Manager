package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
import hr.projekt.app.ProjektJavaApp.Model.Admin;
import hr.projekt.app.ProjektJavaApp.Utils.AdminUtils;
import hr.projekt.app.ProjektJavaApp.Utils.DatabaseUtils;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AdminLogin {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private ImageView imageView;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String ADMIN_FILE = "loginFiles/admin.txt";
    private Map<String, String> adminCredentials = new HashMap<>();

    public void initialize() {
        try {
            InputStream stream = getClass().getResourceAsStream("/img/first.jpg");

            if (stream != null) {
                Image image = new Image(stream);
                imageView.setImage(image);
                double desiredWidth = 600;
                double desiredHeight = 600;
                imageView.setFitWidth(desiredWidth);
                imageView.setFitHeight(desiredHeight);
            } else {
                System.out.println("Image file not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnToFirstPage(){
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource(
                        "firstScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getStage().setTitle("Log In");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adminLogIn(){
        loadAdminCredentials();
        String usernameString = username.getText();
        String passwordString = password.getText();

        String hashedPassword = adminCredentials.get(usernameString);
        if(hashedPassword!=null && BCrypt.checkpw(passwordString,hashedPassword)){
            logger.info("Login successful!");
            System.out.println("Login successful!");

            Admin admin = new Admin(usernameString, hashedPassword);
            AdminUtils.setAdmin(admin);

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
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong username or password.");
            alert.setHeaderText("Type in correct username and password.");
            alert.showAndWait();

            logger.info("Invalid username or password.");
            System.out.println("Invalid username or password.");
        }
    }

    private void loadAdminCredentials() {
        try (Scanner scanner = new Scanner(new File(ADMIN_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                adminCredentials.put(parts[0], parts[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
