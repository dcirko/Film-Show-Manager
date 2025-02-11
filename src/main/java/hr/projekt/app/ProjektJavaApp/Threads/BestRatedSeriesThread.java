package hr.projekt.app.ProjektJavaApp.Threads;

import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Series;
import hr.projekt.app.ProjektJavaApp.Utils.ThreadUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.Optional;

public class BestRatedSeriesThread extends ThreadUtils implements Runnable {
    private final TextArea findBestRatedSeries;

    public BestRatedSeriesThread(TextArea findBestRatedSeries) {
        this.findBestRatedSeries = findBestRatedSeries;
    }

    @Override
    public void run() {
        while (true) {
            Optional<Series> bestRatedSeries = super.findMostExpensiveSeries();
            if (bestRatedSeries.isPresent()) {

                Series bestSeries = bestRatedSeries.get();

                Platform.runLater(() -> {
                    findBestRatedSeries.setText(bestSeries.getTitle() + "-" + bestSeries.getRating());
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
