package hr.projekt.app.ProjektJavaApp.Model;

import java.io.Serializable;

public class WatchlistItem implements Serializable {
    private Integer id;
    private String type;
    private String name;

    public WatchlistItem(Integer id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Type='" + type + '\'' +
                ", Name='" + name + '\'';
    }
}
