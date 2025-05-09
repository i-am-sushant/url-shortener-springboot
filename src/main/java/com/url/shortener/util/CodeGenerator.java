package com.url.shortener.util;

import com.url.shortener.repository.UrlRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CodeGenerator {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_URL_LENGTH = 6;
    private final Random random = new Random();
    private final UrlRepository urlRepository;

    public CodeGenerator(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // Generate a unique alphanumeric ID
    public String generateUniqueShortUrl() {
        String urlId;
        do {
            urlId = generateRandomAlphanumeric(SHORT_URL_LENGTH);
        } while (urlRepository.findByUrlId(urlId).isPresent()); // Ensure uniqueness
        return urlId;
    }

    // Generate random alphanumeric string
    private String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
