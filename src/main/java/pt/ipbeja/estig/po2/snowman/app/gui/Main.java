package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;

import static pt.ipbeja.estig.po2.snowman.app.gui.StartMenu.show;

public class Main extends Application {

    private MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        playBackgroundMusic(); // Música de fundo
        show(primaryStage);    // Mostra o menu
    }

    private void playBackgroundMusic() {

        URL resource = getClass().getResource("/SnowmanMusic.mp3");
        if (resource == null) {
            System.err.println("Ficheiro de música não encontrado.");
            return;
        }

        Media media = new Media(resource.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.5);
        mediaPlayer.play();


    }
}
