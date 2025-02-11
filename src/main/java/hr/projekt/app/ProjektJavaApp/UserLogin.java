package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Exceptions.DuplicateUserException;
import hr.projekt.app.ProjektJavaApp.Exceptions.NoUserFoundException;
import hr.projekt.app.ProjektJavaApp.Genericsi.UserPair;
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

import java.io.*;
import java.util.*;

public class UserLogin {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private ImageView imageView;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String USER_FILE = "loginFiles/user.txt";
    private static final int LOG_ROUNDS = 12;
    private Map<String, String> userCredentials = new HashMap<>();

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

    public void logIn(){
        try {
            loadUserCredentials();
            String usernameString = username.getText();
            String passwordString = password.getText();

            String hashedPassword = userCredentials.get(usernameString);
            if (hashedPassword != null && BCrypt.checkpw(passwordString, hashedPassword)) {
                logger.info("Login successful!");
                System.out.println("Login successful!");
                UserPair<String, String> user = new UserPair<>(usernameString, hashedPassword);
                UserUtils.setLoggedInUser(user);
                Set<UserPair<String, String>> userPairSet = new HashSet<>();
                userPairSet.add(user);

                newScreen();
            } else {
                noUserFoundThrow();
            }
        } catch (NoUserFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong username or password.");
            alert.setHeaderText("Type in correct username and password.");
            alert.showAndWait();
            logger.error("No user was found. Enter correct username and password.");
        }
    }

    private static void noUserFoundThrow() {
        throw new NoUserFoundException("No user was found. Enter correct username and password.");
    }

    public void register() {
        loadUserCredentials();
        String usernameString = username.getText();
        String passwordString = password.getText();

        if(checkForError()) {
            try (FileWriter writer = new FileWriter(USER_FILE, true)) {
                String hashedPassword = BCrypt.hashpw(passwordString, BCrypt.gensalt(LOG_ROUNDS));

                for (String username : userCredentials.keySet()) {
                    try{
                        throwDuplicateException(username, usernameString);
                    }catch (DuplicateUserException e){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Registration unsuccesfull.");
                        alert.setHeaderText("User already exists.");
                        alert.showAndWait();

                        logger.error("User already exists.");
                        return;
                    }
                }
                writer.write(usernameString + "," + hashedPassword + System.lineSeparator());
                logger.info("User added successfully!");
                UserPair<String, String> user = new UserPair<>(usernameString, hashedPassword);
                UserUtils.setLoggedInUser(user);
                Set<UserPair<String, String>> userPairSet = new HashSet<>();
                userPairSet.add(user);

                newScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration unsuccesfull.");
            alert.setHeaderText("You have to type in both username and password.");
            alert.showAndWait();

            logger.info("Invalid username or password.");
            System.out.println("Invalid username or password.");
        }
    }

    private static void throwDuplicateException(String username, String usernameString) throws DuplicateUserException {
        if(username.equals(usernameString)){
            throw new DuplicateUserException("User already exists. Try with new username and password.");
        }
    }

    private static void newScreen() {
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

    private void loadUserCredentials() {
        try (Scanner scanner = new Scanner(new File(USER_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                userCredentials.put(parts[0], parts[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public boolean checkForError(){
        String usernameString = username.getText();
        String passwordString = password.getText();
        if(usernameString.isEmpty() || passwordString.isEmpty()){
            return false;
        }else{
            return true;
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

}
