package hr.projekt.app.ProjektJavaApp.Model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ChangeLog implements Serializable {
    private String changedData;
    private String oldValue;
    private String newValue;
    private String role;
    private String dateTime;

    @Serial
    private static final long serialVersionUID = 1L;

    public ChangeLog(String changedData, String oldValue, String newValue, String role, String dateTime) {
        this.changedData = changedData;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.role = role;
        this.dateTime = dateTime;
    }

    public String getChangedData() {
        return changedData;
    }

    public void setChangedData(String changedData) {
        this.changedData = changedData;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "ChangeLog{" +
                "changedData='" + changedData + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", role='" + role + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
