package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;

import static pt.ipbeja.estig.po2.snowman.app.gui.StartMenu.show;

/**
 * Main is the JavaFX application entry point. It starts background music
 * and then displays the start menu.
 */

public class Main extends Application {

    private MediaPlayer mediaPlayer;

    /**
     * Application entry point. Launches the JavaFX runtime.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called by the JavaFX framework when the application starts.
     * Begins background music and shows the start menu on the primary stage.
     *
     * @param primaryStage the main window provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        playBackgroundMusic();
        show(primaryStage);
    }

    /**
     * Attempts to load and play "SnowmanMusic.mp3" from resources.
     * If the file is not found, logs an error. Otherwise, creates a MediaPlayer,
     * sets it to loop indefinitely at half volume, and starts playback.
     */
    private void playBackgroundMusic() {
        // Locate the MP3 file in the resources folder
        URL resource = getClass().getResource("/SnowmanMusic.mp3");
        if (resource == null) {
            System.err.println("Ficheiro de música não encontrado.");
            return;
        }

        /// Create a Media object from the resource URL
        Media media = new Media(resource.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.5);
        mediaPlayer.play();


    }
}
