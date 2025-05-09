package com.url.shortener.repository;

import com.url.shortener.entity.Url;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByUrlId(String urlId);

    @Modifying
    @Transactional
    void deleteByExpirationDateBefore(LocalDateTime now); // Deletes expired URLs

    @Modifying
    @Transactional
    void deleteByUrlId(String urlId);
}
