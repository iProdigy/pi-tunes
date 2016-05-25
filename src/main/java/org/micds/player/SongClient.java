package org.micds.player;

import lombok.Synchronized;
import org.micds.TastyTunes;
import org.micds.req.SongRequest;
import org.micds.util.HttpUtil;
import org.micds.web.WebController;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.Queue;

public class SongClient {
    private static final Queue<SongRequest> requests = TastyTunes.getRequestQueue();
    private final PlayerUI gui = new PlayerUI();

    @Synchronized
    public void update() {
        if (requests.size() > 0) {
            SongRequest next = requests.peek();
            Optional<File> mp3 = next.getFile();

            if (!mp3.isPresent()) {
                HttpUtil.downloadMP3(TastyTunes.getSongDirectory(), next.getLink()); // TODO: Don't download really long videos
                mp3 = next.getFile();

                // File not present after downloading, let's move on
                if (!mp3.isPresent()) {
                    requests.remove(next);
                    LoggerFactory.getLogger(WebController.class).error("Unable to download and play %s.", next.toString());
                    return;
                }
            }

            if (!gui.isPlaying() && mp3.isPresent()) {
                requests.remove(next);
                gui.playSong(mp3.get());
            }
        }
    }

    public void updateFromNewThread() {
        new Thread(this::update).start(); // TODO: can this be done more elegantly?
    }

}
