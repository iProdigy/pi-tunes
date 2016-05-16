package org.micds.player;

import jaco.mp3.player.MP3Player;
import org.micds.req.RequestQueue;
import org.micds.req.SongRequest;
import org.micds.util.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class SongClient {
    private static final SongClient INSTANCE;
    private static final RequestQueue requests;
    private static final String SONG_DIR;
    private MP3Player player; // TODO: Consider changing to a better library... see MP3SPI
    private boolean downloading = false; // TODO: Is volatile needed?

    static {
        // TODO: Store songs from different websites in different folders
        SONG_DIR = System.getProperty("user.home") + "\\Songs\\";
        new File(SONG_DIR).mkdirs(); // make sure the directory exists

        requests = RequestQueue.getQueue();
        INSTANCE = new SongClient();
    }

    public void update() {
        if (requests.size() > 0 && !downloading) {
            SongRequest next = requests.getFirst();
            Optional<File> mp3 = getMp3(next);

            // TODO: Make sure we have always downloaded the next one
            if (!mp3.isPresent()) {
                this.downloading = true;
                HttpUtil.downloadMP3(SONG_DIR, next.getLink()); // TODO: Don't download really long videos
                this.downloading = false;

                mp3 = getMp3(next);
            }

            if (!isPlaying() && mp3.isPresent()) {
                File songFile = mp3.get();
                requests.remove(next);
                this.setSong(songFile); // TODO: Reuse same player
                this.play();
            }
        }
    }

    private void setSong(File file) {
        this.player = new MP3Player(file);
        player.setRepeat(false);
        player.setShuffle(false);
    }

    private void play() {
        player.play();

        // terrible way to call update once done playing
        new Thread(() -> {
            while (isPlaying()) {
                try {
                    Thread.sleep(1000); // wait a second before checking again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            update();
        }).start(); // TODO: More elegant solution
    }

    private boolean isPlaying() {
        return player != null && !player.isPaused() && !player.isStopped();
    }

    private Optional<File> getMp3(SongRequest req) {
        Optional<String> id = HttpUtil.getID(req);
        String search = id.isPresent() ? id.get() : req.getTitle();

        // TODO: More reliable searching

        try (Stream<Path> paths = Files.walk(Paths.get(SONG_DIR))) {
            Optional<Path> filePath = paths.filter(path -> path.toFile().isFile())
                    .filter(path -> path.toFile().getName().endsWith(".mp3"))
                    .filter(path -> path.toFile().getName().startsWith(search))
                    .findFirst();

            if (filePath.isPresent()) {
                return Optional.of(filePath.get().toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static SongClient getInstance() {
        return INSTANCE;
    }

}
