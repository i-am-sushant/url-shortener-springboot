package com.url.shortener.validator;

import com.url.shortener.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class UrlIdValidator {

    // Pattern for alphanumeric URL IDs of exactly 6 characters
    private static final String URL_ID_REGEX = "^[a-zA-Z0-9]{6}$";
    private static final Pattern PATTERN = Pattern.compile(URL_ID_REGEX);

    public static void validateUrlId(String urlId) {
        if (urlId == null || urlId.length() != 6 || !PATTERN.matcher(urlId).matches()) {
            log.warn("URL ID: {} must be exactly 6 alphanumeric characters.", urlId);
            throw new BadRequestException("URL ID must be exactly 6 alphanumeric characters.");
        }
    }
}
