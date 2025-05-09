package com.url.shortener.scheduler;

import com.url.shortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class UrlCleanupScheduler {

    private final UrlRepository urlRepository;

    public UrlCleanupScheduler(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // Run every minute to clean up expired URLs
    @Scheduled(fixedRateString = "${scheduler.cleanup-interval-ms}") // Use fixedRateString for dynamic values
    public void cleanUpExpiredUrls() {
        log.info("THE SCHEDULER IS NOW RUNNING");
        urlRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }
}
