package org.micds;

import javafx.application.Application;
import org.micds.player.PlayerUI;
import org.micds.player.SongClient;
import org.micds.req.SongRequest;
import org.micds.web.WebController;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

public class TastyTunes {
    private static final Queue<SongRequest> REQUEST_QUEUE = new ArrayDeque<>();
    private static final String SONG_DIR = System.getProperty("user.home") + "\\Songs\\";
    private static final SongClient SONG_CLIENT = new SongClient();
    private static ApplicationContext APP_CONTEXT;

    public static void main(String[] args) {
        new File(SONG_DIR).mkdirs(); // make sure the song directory exists
        APP_CONTEXT = SpringApplication.run(WebController.class, args); // Start the web server
        SONG_CLIENT.update(); // have the Song Client running
        Application.launch(PlayerUI.class, args); // start the javafx gui
    }

    public static SongClient getSongClient() {
        return SONG_CLIENT;
    }

    public static Queue<SongRequest> getRequestQueue() {
        return REQUEST_QUEUE;
    }

    public static String getSongDirectory() {
        return SONG_DIR; // TODO: Store songs from different websites in different folders
    }

    public static ApplicationContext getWebAppContext() {
        return APP_CONTEXT;
    }

}
