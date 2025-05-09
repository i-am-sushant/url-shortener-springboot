package com.url.shortener.transfomer;

import com.url.shortener.dto.CreateShortUrlDto;
import com.url.shortener.entity.Url;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class UrlTransformer {

    public Url mapDtoToEntity(CreateShortUrlDto dto){
        Url entity = new Url();
        entity.setOriginalUrl(dto.getOriginalUrl());
        entity.setUrlId(dto.getUrlId());
        entity.setCreatedAt(LocalDate.now());
        entity.setUpdatedAt(LocalDate.now());

        // If TTL is provided, calculate the expiration date in seconds
        if (dto.getTtl() != null && dto.getTtl() > 0) {
            entity.setExpirationDate(LocalDateTime.now().plusSeconds(dto.getTtl()));
        } else {
            // URL should remain forever (no expiration)
            entity.setExpirationDate(null);
        }

        return entity;
    }

    public CreateShortUrlDto mapEntityToDto(Url entity) {
        CreateShortUrlDto dto = new CreateShortUrlDto();
        dto.setId(entity.getId());
        dto.setOriginalUrl(entity.getOriginalUrl());
        dto.setUrlId(entity.getUrlId());

        // Handle TTL calculation
        if (entity.getExpirationDate() != null) {
            long ttl = Duration.between(LocalDateTime.now(), entity.getExpirationDate()).getSeconds();
            dto.setTtl(ttl > 0 ? ttl : 0); // Set to 0 if TTL is negative (expired)
        } else {
            dto.setTtl(null); // or 0 if you want TTL for indefinite URLs to be 0
        }

        return dto;
    }

}
