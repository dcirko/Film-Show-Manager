package hr.projekt.app.ProjektJavaApp.Utils;

import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Series;

import java.util.Optional;

public class ThreadUtils {
    private static boolean movieDatabaseInProgress = false;
    private static boolean seriesDatabaseInProgress = false;

    public synchronized Optional<Movie> findMostExpensiveMovie() {

        while(movieDatabaseInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        movieDatabaseInProgress = true;

        Optional<Movie> bestRatedMovie = DatabaseUtils.findBestRatedMovie();

        movieDatabaseInProgress = false;

        notifyAll();

        return bestRatedMovie;
    }

    public synchronized Optional<Series> findMostExpensiveSeries() {

        while(seriesDatabaseInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        seriesDatabaseInProgress = true;

        Optional<Series> bestRatedSeries = DatabaseUtils.findBestRatedSeries();

        seriesDatabaseInProgress = false;

        notifyAll();

        return bestRatedSeries;
    }
}
