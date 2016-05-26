package org.micds.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;
import org.micds.PiTunes;
import org.micds.util.HttpUtil;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = "link")
public class SongRequest {

    @NotNull
    @NotEmpty
    @org.hibernate.validator.constraints.URL
    private String link;
    private String title;
    private String id;
    private File file;

    public String getTitle() {
        if (title != null)
            return title;

        try {
            return (title = HttpUtil.getTitle(this.toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<String> getID() {
        if (id != null)
            return Optional.of(id);

        return HttpUtil.getID(this);
    }

    public Optional<File> getFile() {
        if (file != null && file.exists())
            return Optional.of(file);

        final Optional<String> id = this.getID();
        final String search = (id.isPresent() ? id.get() : this.getTitle()) + ".mp3";

        try (Stream<Path> paths = Files.walk(Paths.get(PiTunes.getSongDirectory()))) {
            Optional<Path> filePath = paths.filter(path -> path.toFile().isFile())
                    .filter(path -> path.toFile().getName().equalsIgnoreCase(search))
                    .findFirst();

            if (filePath.isPresent()) {
                file = filePath.get().toFile();
                return Optional.of(this.file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public URL toURL() throws MalformedURLException {
        return new URL(link);
    }

}
