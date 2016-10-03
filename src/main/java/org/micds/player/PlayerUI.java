package org.micds.player;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.micds.PiTunes;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.net.URISyntaxException;

public class PlayerUI extends Application {
    private MediaPlayer player;
    private MediaView view;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Pi Tunes");
        primaryStage.setScene(getScene(800, 480));
        primaryStage.show();

        // properly shut down on close
        primaryStage.setOnCloseRequest(e -> {
            SpringApplication.exit(PiTunes.getWebAppContext(), () -> 0); // stop server
            Platform.exit(); // stop gui
            System.exit(0); // close all other threads
        });
    }

    private Scene getScene(final double width, final double height) {
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, width, height);

        try {
            this.player = new MediaPlayer(new Media(getClass().getClassLoader().getResource("startup.mp3").toURI().toString()));
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            return scene;
        }

        player.setAutoPlay(true);

        this.view = new MediaView(this.player);
        pane.setCenter(view);

        // TODO: Fancy GUI

        return scene;
    }

    public void setSong(final File songFile) {
        if (player != null && player.getStatus() != MediaPlayer.Status.DISPOSED) {
            if (player.getStatus() == MediaPlayer.Status.PLAYING)
                player.stop();

            player.dispose();
        }

        this.player = new MediaPlayer(new Media(songFile.toURI().toString()));

        if (this.view == null) {
            view = new MediaView(this.player);
        } else {
            view.setMediaPlayer(this.player);
        }
    }

    public void play() {
        if (player == null)
            return;

        player.play();
        player.setOnEndOfMedia(() -> {
            player.stop();
            player.dispose();
            PiTunes.getSongClient().updateFromNewThread();
        });
    }

    public void playSong(final File file) {
        this.setSong(file);
        this.play();
    }

    public boolean isPlaying() {
        return (this.player != null) && (this.player.getStatus() == MediaPlayer.Status.PLAYING);
    }

}
