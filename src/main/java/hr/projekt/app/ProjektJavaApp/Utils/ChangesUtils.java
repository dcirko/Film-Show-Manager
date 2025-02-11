package hr.projekt.app.ProjektJavaApp.Utils;

import hr.projekt.app.ProjektJavaApp.Model.ChangeLog;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChangesUtils {
    private static final String CHANGELOG_FILE = "binaryFile/serialized.txt";

    public static void logChange(String changedData, String oldValue, String newValue, String role) {
        try {
            List<ChangeLog> changeList = deserializationOfChanges();
            LocalDateTime localDateTime = LocalDateTime.now();
            ChangeLog change = new ChangeLog(changedData, oldValue, newValue, role, localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")));
            changeList.add(change);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(CHANGELOG_FILE))) {
                outputStream.writeObject(changeList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ChangeLog> deserializationOfChanges() {
        File file = new File(CHANGELOG_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(CHANGELOG_FILE))) {
            return (List<ChangeLog>) inputStream.readObject();
        } catch(EOFException ignored){
            return new ArrayList<>();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
