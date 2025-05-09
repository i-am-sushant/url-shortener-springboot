package com.url.shortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Schema(description = "Data transfer object for creating a shortened URL")
public class CreateShortUrlDto {

    private Long id;

    @NotEmpty(message = "Original URL cannot be empty")
    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,}(\\/.*)?$",
            message = "Invalid URL format.")
    @Schema(description = "The original URL to be shortened", example = "https://www.example.com")
    private String originalUrl;

    @Size(min = 6, max = 6, message = "URL ID must be exactly 6 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "URL ID must be alphanumeric.")
    @Schema(description = "Custom URL ID for the shortened URL", example = "abc123", nullable = true)
    private String urlId;

    @Min(1)
    @Schema(description = "Time-to-live (TTL) in seconds for the shortened URL", example = "3600", nullable = true)
    private Long ttl;

}
