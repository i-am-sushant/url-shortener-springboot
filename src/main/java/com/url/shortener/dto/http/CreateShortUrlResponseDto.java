package com.url.shortener.dto.http;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Response DTO for creating a shortened URL")
public class CreateShortUrlResponseDto {

    @Schema(description = "The unique ID of the created URL", example = "1")
    private Long id;

    @Schema(description = "The original URL provided by the user", example = "https://www.example.com")
    private String originalUrl;

    @Schema(description = "The time-to-live (TTL) in seconds for the shortened URL", example = "3600", nullable = true)
    private Long ttl;

    @Schema(description = "The generated or user-provided short URL ID", example = "abc123")
    private String urlId;
}
