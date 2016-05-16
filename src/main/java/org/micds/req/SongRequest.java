package org.micds.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;
import org.micds.util.HttpUtil;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

@Data
@EqualsAndHashCode(of = "link")
public class SongRequest {

    @NotNull
    @NotEmpty
    @org.hibernate.validator.constraints.URL
    private String link;

    private String title;

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

    public URL toURL() throws MalformedURLException {
        return new URL(link);
    }

}
