package org.micds.player;

import lombok.Synchronized;
import org.micds.TastyTunes;
import org.micds.req.SongRequest;
import org.micds.util.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Stream;

public class SongClient {
    private static final Queue<SongRequest> requests = TastyTunes.getRequestQueue();
    private final PlayerUI gui = new PlayerUI();

    @Synchronized
    public void update() {
        if (requests.size() > 0) {
            SongRequest next = requests.peek();
            Optional<File> mp3 = getMp3(next);

            if (!mp3.isPresent()) {
                HttpUtil.downloadMP3(TastyTunes.getSongDirectory(), next.getLink()); // TODO: Don't download really long videos
                mp3 = getMp3(next);
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

    private Optional<File> getMp3(final SongRequest req) {
        final Optional<String> id = HttpUtil.getID(req);
        final String search = id.isPresent() ? id.get() : req.getTitle();

        // TODO: More reliable searching

        try (Stream<Path> paths = Files.walk(Paths.get(TastyTunes.getSongDirectory()))) {
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

}
