package org.micds.req;

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
            String host = url.getHost();

            boolean valid = host.contains("youtube.com") || host.contains("youtu.be")
                    || host.contains("soundcloud.com");

            if (!valid) {
                errors.reject(ERROR_CODE);
            }
        } catch (IOException e) {
            errors.reject(ERROR_CODE);
        }
    }

}
