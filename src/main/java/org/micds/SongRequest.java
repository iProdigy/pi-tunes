package org.micds;

import lombok.Data;

import java.net.MalformedURLException;
import java.net.URL;

@Data
public class SongRequest {
    private String link;

    public URL toURL() throws MalformedURLException {
        return new URL(link);
    }

    public boolean hasErrors(String checkLink) {
        return false; // TODO: Input Validation
    }
}
