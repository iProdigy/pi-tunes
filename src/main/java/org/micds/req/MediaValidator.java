package org.micds.req;

import org.micds.util.HttpUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.net.URL;

public class MediaValidator implements Validator {
    // TODO: Use a custom error code
    private static final String ERROR_CODE = "org.hibernate.validator.constraints.URL.message";

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == SongRequest.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        SongRequest req = (SongRequest) target;

        try {
            URL url = req.toURL();
            String host = url.getHost().toLowerCase();
            String addr = req.getLink().toLowerCase();

            final String ytLink = HttpUtil.YT_LINK, ytSLink = "youtu.be", soundLink = HttpUtil.SC_LINK;

            final boolean yt = host.contains(ytLink),
                    ytShort = host.contains(ytSLink),
                    sc = host.contains(soundLink);

            if (!yt && !ytShort && !sc) {
                errors.reject(ERROR_CODE);
            } else {
                if (yt) {
                    if (!addr.contains(HttpUtil.YT_WATCH)) {
                        errors.reject(ERROR_CODE);
                    }
                } else {
                    final int index = ytShort ? addr.indexOf(ytSLink) : addr.indexOf(soundLink);
                    final int extra = addr.length() - (index + (ytShort ? ytSLink.length() : soundLink.length()));

                    System.out.println(extra);

                    if (extra < 3)
                        errors.reject(ERROR_CODE);
                }
            }
        } catch (IOException e) {
            errors.reject(ERROR_CODE);
        }
    }

}
