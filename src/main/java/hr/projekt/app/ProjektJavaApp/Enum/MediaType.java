package hr.projekt.app.ProjektJavaApp.Enum;

public enum MediaType {
    MOVIE("MOVIE"),
    SERIES("SERIES");
    private String name;

    MediaType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
