package hr.projekt.app.ProjektJavaApp.Model;

public class Actor {
    private final String name;
    private final String surname;

    private Actor(Builder builder) {
        this.name = builder.name;
        this.surname = builder.surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public static class Builder {
        private String name;
        private String surname;

        public Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Actor build() {
            return new Actor(this);
        }
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }
}

