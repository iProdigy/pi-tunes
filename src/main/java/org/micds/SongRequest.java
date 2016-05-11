package org.micds;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

@Data
public class SongRequest {
    private final RequestQueue requests = RequestQueue.getQueue();

    @NotNull
    @NotEmpty
    @org.hibernate.validator.constraints.URL
    private String link;

    public URL toURL() throws MalformedURLException {
        return new URL(link);
    }

}
