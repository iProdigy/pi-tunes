package org.micds;

import org.micds.player.SongClient;
import org.micds.web.WebController;
import org.springframework.boot.SpringApplication;

public class TastyTunes {

    public static void main(String[] args) {
        SpringApplication.run(WebController.class, args);
        SongClient.getInstance().update();
    }

}
