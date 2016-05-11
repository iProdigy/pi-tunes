package org.micds;

import lombok.Data;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;

import java.net.MalformedURLException;
import java.net.URL;

@Data
public class SongRequest {
    private final RequestQueue requests = RequestQueue.getQueue();

    @org.hibernate.validator.constraints.URL
    private String link;

    public URL toURL() throws MalformedURLException {
        return new URL(link);
    }

    public boolean hasErrors(String checkLink) {
        return false; // TODO: Input Validation
    }
}
