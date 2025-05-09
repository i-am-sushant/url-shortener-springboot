package com.url.shortener.dto.http;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "Response DTO for successful URL deletion")
public class DeleteShortUrlResponseDto {

    @Schema(description = "Confirmation message for successful deletion", example = "URL deleted successfully.")
    private String message;
}
