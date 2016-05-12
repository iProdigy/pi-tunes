package org.micds.req;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.micds.util.HttpUtil;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "link")
public class SongRequest {
    private final RequestQueue requests = RequestQueue.getQueue();

    private String title;

    @NotNull
    @NotEmpty
    @org.hibernate.validator.constraints.URL
    private String link;

    public URL toURL() throws MalformedURLException {
        return new URL(link);
    }

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

}
