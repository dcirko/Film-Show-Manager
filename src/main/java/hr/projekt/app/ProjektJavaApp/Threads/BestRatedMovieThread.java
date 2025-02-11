package hr.projekt.app.ProjektJavaApp.Threads;

import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Utils.ThreadUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.Optional;

public class BestRatedMovieThread extends ThreadUtils implements Runnable{
    private final TextArea findBestRatedMovie;

    public BestRatedMovieThread(TextArea findBestRatedMovie) {
        this.findBestRatedMovie = findBestRatedMovie;
    }

    @Override
    public void run() {
        while (true) {
            Optional<Movie> bestRatedMovie = super.findMostExpensiveMovie();
            if (bestRatedMovie.isPresent()) {

                Movie bestMovie = bestRatedMovie.get();

                Platform.runLater(() -> {
                    findBestRatedMovie.setText(bestMovie.getTitle() + "-" + bestMovie.getRating());
                });
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
