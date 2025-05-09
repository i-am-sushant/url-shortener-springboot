package com.url.shortener.controller;

import com.url.shortener.dto.CreateShortUrlDto;
import com.url.shortener.dto.http.CreateShortUrlResponseDto;
import com.url.shortener.dto.http.DeleteShortUrlResponseDto;
import com.url.shortener.dto.http.ErrorResponseDto;
import com.url.shortener.entity.Url;
import com.url.shortener.exception.ResourceNotFoundException;
import com.url.shortener.repository.UrlRepository;
import com.url.shortener.service.IUrlService;
import com.url.shortener.transfomer.UrlTransformer;
import com.url.shortener.validator.UrlIdValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class UrlController {

    private final IUrlService service;
    private final UrlTransformer transformer;

    private final UrlRepository repository;

    public UrlController(IUrlService service, UrlTransformer transformer, UrlRepository repository) {
        this.service = service;
        this.transformer = transformer;
        this.repository = repository;
    }

    @Operation(summary = "Create a short URL", description = "Creates a new shortened URL from an original URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "URL successfully shortened",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateShortUrlResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<CreateShortUrlResponseDto> createUrl(@Valid @RequestBody CreateShortUrlDto formData) {
        Url url = transformer.mapDtoToEntity(formData);
        CreateShortUrlDto createdUrl = service.createShortUrl(url);
        CreateShortUrlResponseDto responseDto = new CreateShortUrlResponseDto(createdUrl.getId(),
                createdUrl.getOriginalUrl(),
                createdUrl.getTtl(),
                createdUrl.getUrlId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Redirect to the original URL", description = "Fetches the original URL and redirects to it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirection to original URL"),
            @ApiResponse(responseCode = "404", description = "Short URL not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{urlId}")
    public RedirectView redirectToOriginalUrl(@PathVariable String urlId) {

        Optional<Url> foundUrl = repository.findByUrlId(urlId);

        if (foundUrl.isEmpty()) {
            log.warn("URL with ID {} not found.", urlId);
            throw new ResourceNotFoundException("URL with ID " + urlId + " not found.");
        }

        // Redirect to the original URL
        return new RedirectView(foundUrl.get().getOriginalUrl());
    }

    @Operation(summary = "Delete a short URL", description = "Deletes a shortened URL by its short URL ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL successfully deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeleteShortUrlResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "URL not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/delete/{urlId}")
    public ResponseEntity<DeleteShortUrlResponseDto> deleteUrl(@PathVariable String urlId){
        // Validate the URL ID
        UrlIdValidator.validateUrlId(urlId);

        Optional<Url> foundUrl = repository.findByUrlId(urlId);

        if (foundUrl.isEmpty()) {
            log.warn("URL with ID {} not found for deletion.", urlId);
            throw new ResourceNotFoundException("URL with ID " + urlId + " not found.");
        }

        repository.deleteByUrlId(urlId);
        return new ResponseEntity<>(new DeleteShortUrlResponseDto("URL deleted successfully."), HttpStatus.OK);
    }
}
