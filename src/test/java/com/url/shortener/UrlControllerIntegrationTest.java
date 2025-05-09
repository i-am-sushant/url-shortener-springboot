package com.url.shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.url.shortener.dto.CreateShortUrlDto;
import com.url.shortener.dto.http.DeleteShortUrlResponseDto;
import com.url.shortener.entity.Url;
import com.url.shortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Starts the full Spring context for integration testing
@AutoConfigureMockMvc // Auto-configures MockMvc to simulate HTTP requests
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")  // Explicitly load application-test.yml
public class UrlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ObjectMapper objectMapper; // To convert objects to JSON strings and vice versa

    @BeforeEach
    public void setup() {
        // Clear the H2 database before each test
        urlRepository.deleteAll();
    }
    // Test: Create Short URL with user-provided urlId
    @Test
    public void shouldCreateShortUrl() throws Exception {
        CreateShortUrlDto createShortUrlDto = new CreateShortUrlDto();
        createShortUrlDto.setOriginalUrl("https://www.example.com");
        createShortUrlDto.setUrlId("abc123");  // Provided by user
        createShortUrlDto.setTtl(3600L);  // 1 hour TTL

        mockMvc.perform(post("/api/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createShortUrlDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.urlId").value("abc123"));  // Verify provided urlId
    }

    // Test: Create Short URL without providing urlId (expect system-generated value)
    @Test
    public void shouldCreateShortUrlWithoutUserId() throws Exception {
        CreateShortUrlDto createShortUrlDto = new CreateShortUrlDto();
        createShortUrlDto.setOriginalUrl("https://www.example.com");
        createShortUrlDto.setTtl(3600L);  // 1 hour TTL
//        createShortUrlDto.setUrlId(null);  // urlId not provided by user

        MvcResult result = mockMvc.perform(post("/api/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createShortUrlDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.urlId").isString())  // Expect generated urlId to be a string
                .andReturn();

        // Extract generated urlId from the response and assert it's of expected length
        String responseJson = result.getResponse().getContentAsString();
        String generatedUrlId = objectMapper.readTree(responseJson).get("urlId").asText();
        assertThat(generatedUrlId).hasSize(6);  // Assuming urlId length is 6
    }

    // Test: Create Short URL without providing ttl (expect ttl to be null or not present)
    @Test
    public void shouldCreateShortUrlWithoutTtl() throws Exception {
        CreateShortUrlDto createShortUrlDto = new CreateShortUrlDto();
        createShortUrlDto.setOriginalUrl("https://www.example.com");
        createShortUrlDto.setUrlId("xyz789");  // Provided by user
        createShortUrlDto.setTtl(null);  // TTL not provided

        mockMvc.perform(post("/api/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createShortUrlDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.urlId").value("xyz789"))  // Verify provided urlId
                .andExpect(jsonPath("$.ttl").isEmpty());  // Expect TTL to be null or not present
    }

    // Test: Redirect to the original URL using URL ID
    @Test
    public void shouldRedirectToOriginalUrl() throws Exception {
        // Create a URL entity in the test H2 database
        Url url = new Url();
        url.setOriginalUrl("https://www.google.com");
        url.setUrlId("xyz789");
        url.setCreatedAt(LocalDate.now());
        url.setUpdatedAt(LocalDate.now());
        urlRepository.save(url);

        mockMvc.perform(get("/api/v1/xyz789"))
                .andExpect(status().is3xxRedirection()) // 3xx indicates a redirection
                .andExpect(redirectedUrl("https://www.google.com"));
    }

    // Test: Delete URL by its ID
    @Test
    public void shouldDeleteUrl() throws Exception {
        // Create a URL entity in the test H2 database with a valid 6-character urlId
        Url url = new Url();
        url.setOriginalUrl("https://www.reddit.com");
        url.setUrlId("abc123");  // Ensure the urlId is exactly 6 alphanumeric characters
        url.setCreatedAt(LocalDate.now());
        url.setUpdatedAt(LocalDate.now());
        urlRepository.save(url);

        // Send delete request
        MvcResult result = mockMvc.perform(delete("/api/v1/delete/abc123"))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the response message
        String responseJson = result.getResponse().getContentAsString();
        DeleteShortUrlResponseDto response = objectMapper.readValue(responseJson, DeleteShortUrlResponseDto.class);
        assertThat(response.getMessage()).isEqualTo("URL deleted successfully.");

        // Verify that the URL is deleted from the database
        assertThat(urlRepository.findByUrlId("abc123")).isEmpty();
    }

    // Test: URL not found for redirection
    @Test
    public void shouldReturn404WhenUrlNotFoundForRedirection() throws Exception {
        mockMvc.perform(get("/api/v1/abc134"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("URL with ID abc134 not found."));
    }

    // Test: URL not found for deletion
    @Test
    public void shouldReturn404WhenUrlNotFoundForDeletion() throws Exception {
        mockMvc.perform(delete("/api/v1/delete/abc134"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("URL with ID abc134 not found."));
    }

    // Test: Attempt to delete URL with an invalid urlId (expect 400 Bad Request)
    @Test
    public void shouldReturn400WhenDeletingWithInvalidUrlId() throws Exception {
        // Try to delete a URL with an invalid urlId (e.g., "invalid1", which is more than 6 characters)
        mockMvc.perform(delete("/api/v1/delete/invalid1"))
                .andExpect(status().isBadRequest())  // Expect 400 Bad Request
                .andExpect(jsonPath("$.errorMessage").value("URL ID must be exactly 6 alphanumeric characters."));  // Assuming this is the validation message in your application
    }

}
