package com.url.shortener.dto.http;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
//import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Schema(description = "Error response containing status code and error message")
public class ErrorResponseDto {

    @Schema(description = "HTTP status code", example = "400")
    private Integer statusCode;

    @Schema(description = "Detailed error message", example = "URL ID must be exactly 6 alphanumeric characters.")
    private String errorMessage;
}
