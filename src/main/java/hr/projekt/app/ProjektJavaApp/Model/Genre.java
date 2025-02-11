package hr.projekt.app.ProjektJavaApp.Model;

import java.io.Serializable;

public class Genre extends NamedEntity implements Serializable {
    private String description;

    public Genre(Integer id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return name;
    }
}
