package hr.projekt.app.ProjektJavaApp.Model;

import java.io.Serializable;
import java.math.BigDecimal;

public non-sealed class Movie extends NamedEntity implements MovieLength, YearOfRelease, Reviewable, Serializable {
    private Integer length;
    private Genre movieGenre;
    private BigDecimal rating;
    private final Integer movieReleaseDate;
    private Revenue movieRevenue;
    private Actor actor;


    public Movie(Integer id, String name, Integer length, Genre movieGenre, BigDecimal rating, Integer movieReleaseDate, Revenue movieRevenue, Actor actor) {
        super(id, name);
        this.length = length;
        this.movieGenre = movieGenre;
        this.rating = rating;
        this.movieReleaseDate = movieReleaseDate;
        this.movieRevenue = movieRevenue;
        this.actor = actor;
    }

    @Override
    public Integer returnMovieLength() {
        return length;
    }

    public Genre getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(Genre movieGenre) {
        this.movieGenre = movieGenre;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }


    public Revenue getMovieRevenue() {
        return movieRevenue;
    }

    public void setMovieRevenue(Revenue movieRevenue) {
        this.movieRevenue = movieRevenue;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
    public Integer releaseYear() {
        return movieReleaseDate;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
