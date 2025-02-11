package hr.projekt.app.ProjektJavaApp;

import hr.projekt.app.ProjektJavaApp.Threads.BestRatedMovieThread;
import hr.projekt.app.ProjektJavaApp.Threads.BestRatedSeriesThread;
import hr.projekt.app.ProjektJavaApp.Utils.UserUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class BestRatedMovieAndSeries {
    @FXML
    private TextArea movieTextArea;
    @FXML
    private TextArea seriesTextArea;

    public void initialize(){
        BestRatedMovieThread bestRatedMovieThread = new BestRatedMovieThread(movieTextArea);
        Thread threadMovie = new Thread(bestRatedMovieThread);
        threadMovie.start();

        BestRatedSeriesThread bestRatedSeriesThread = new BestRatedSeriesThread(seriesTextArea);
        Thread threadSeries = new Thread(bestRatedSeriesThread);
        threadSeries.start();
    }

    public void mainMenu(){
        UserUtils.mainMenu();
    }
}
