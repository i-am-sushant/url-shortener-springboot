# URL Shortener Service

## Project Description

The **URL Shortener Service** is a Spring Boot-based application that allows users to shorten long URLs. It provides endpoints to create short URLs, redirect to the original URLs using the short IDs, and delete the shortened URLs. The service supports setting a custom short URL ID, along with an optional time-to-live (TTL) for the shortened URL. If no TTL is specified, the URL will persist indefinitely. The project uses a PostgreSQL database in production and H2 in-memory database for testing.

## Prerequisites

To run this project locally, you need to have the following tools installed:

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** (for running the PostgreSQL database)
- **PostgreSQL** (if not using Docker)

If using Docker, ensure that the PostgreSQL container is running.

## Endpoints

| HTTP Method | Endpoint              | Description                                    | Request Body Example |
|-------------|-----------------------|------------------------------------------------|----------------------|
| `POST`      | `/api/v1/create`       | Creates a new shortened URL                    | `{ "originalUrl": "https://www.example.com", "urlId": "abc123", "ttl": 3600 }` |
| `GET`       | `/api/v1/{urlId}`      | Redirects to the original URL using the short ID | N/A |
| `DELETE`    | `/api/v1/delete/{urlId}`| Deletes a shortened URL by its ID              | N/A |

### Request Parameters

- **urlId**: Shortened URL ID (Alphanumeric, 6 characters).

### Project Setup

```bash
git clone https://github.com/i-am-sushant/url-shortener-springboot.git
```

```bash
cd url-shortener-springboot
```

Run PostgreSQL in Docker (if PostgreSQL is not installed locally):

```bash
docker run --name pg-url-shortener -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=root -e POSTGRES_DB=shortener_db -p 5432:5432 -d postgres
```

### Build and run the project:

```bash
mvn clean install
```

```bash
mvn spring-boot:run
```

The application will run on http://localhost:8080.

### How to Run Tests:

```bash
mvn test
```
The tests are configured to use the in-memory H2 database, which is set up in the application-test.yml file.


### How to Access swagger documentation:

```bash
http://localhost:8080/swagger-ui.html
```

### Contributor
[Muhire Josué](https://github.com/Muhire-Josue)

### Folder structure:

```json
src
 ├── main
 │    ├── java
 │    │    └── com.url.shortener
 │    │          ├── controller
 │    │          │    └── UrlController.java
 │    │          ├── dto
 │    │          │    ├── CreateShortUrlDto.java
 │    │          │    └── http
 │    │          │          ├── CreateShortUrlResponseDto.java
 │    │          │          └── DeleteShortUrlResponseDto.java
 │    │          ├── entity
 │    │          │    └── Url.java
 │    │          ├── exception
 │    │          │    ├── BadRequestException.java
 │    │          │    ├── GlobalExceptionHandler.java
 │    │          │    └── ResourceNotFoundException.java
 │    │          ├── repository
 │    │          │    └── UrlRepository.java
 │    │          ├── scheduler
 │    │          │    └── UrlCleanupScheduler.java
 │    │          ├── service
 │    │          │    ├── IUrlService.java
 │    │          │    └── UrlServiceImpl.java
 │    │          ├── transfomer
 │    │          │    └── UrlTransformer.java
 │    │          ├── util
 │    │          │    └── CodeGenerator.java
 │    │          └── validator
 │    │                └── UrlIdValidator.java
 │    └── resources
 │          ├── application.yml
 │          └── db/migration
 │                └── V1__Initial.sql
 └── test
      ├── java
      │    └── com.url.shortener
      │          └── UrlControllerIntegrationTest.java
      └── resources
            └── application-test.yml
```
