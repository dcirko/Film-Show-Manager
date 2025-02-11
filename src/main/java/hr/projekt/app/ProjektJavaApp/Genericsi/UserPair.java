package hr.projekt.app.ProjektJavaApp.Genericsi;

public class UserPair<T, U> {
    private T username;
    private U password;

    public UserPair(T username, U password) {
        this.username = username;
        this.password = password;
    }

    public T getUsername() {
        return username;
    }

    public void setUsername(T username) {
        this.username = username;
    }

    public U getPassword() {
        return password;
    }

    public void setPassword(U password) {
        this.password = password;
    }
}
