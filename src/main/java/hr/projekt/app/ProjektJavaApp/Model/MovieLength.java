package hr.projekt.app.ProjektJavaApp.Model;

public sealed interface MovieLength permits Movie{
    Integer returnMovieLength();
}
