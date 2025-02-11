package hr.projekt.app.ProjektJavaApp.Model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Series extends NamedEntity implements YearOfRelease, Reviewable, Serializable {
    private Genre seriesGenre;
    private BigDecimal rating;
    private final Integer releaseDate;
    private Integer numberOfSeasons;
    private Actor actor;

    public Series(Integer id, String name, Genre seriesGenre, BigDecimal rating, Integer releaseDate, Integer numberOfSeasons, Actor actor) {
        super(id, name);
        this.seriesGenre = seriesGenre;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.numberOfSeasons = numberOfSeasons;
        this.actor = actor;
    }

    public Genre getSeriesGenre() {
        return seriesGenre;
    }

    public void setSeriesGenre(Genre seriesGenre) {
        this.seriesGenre = seriesGenre;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
    public Integer releaseYear() {
        return releaseDate;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String toString() {
        return  name;
    }
}
