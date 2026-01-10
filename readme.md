# URL Shortener Service

## Project Description

The **URL Shortener Service** is a Spring Boot-based application that allows users to shorten long URLs. It provides endpoints to create short URLs, redirect to the original URLs using the short IDs, and delete the shortened URLs. The service supports setting a custom short URL ID, along with an optional time-to-live (TTL) for the shortened URL. If no TTL is specified, the URL will persist indefinitely. The project uses a PostgreSQL database in production and H2 in-memory database for testing.

## Prerequisites

To run this project locally, you need to have the following tools installed:

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** (recommended: run app + Postgres via Compose)

Note: This project targets Java 21 (see `pom.xml`).

If using Docker Compose, it will start PostgreSQL for you.

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
docker compose up --build
```

The API will be available at http://localhost:8080.

Swagger UI:

```bash
http://localhost:8080/swagger-ui/index.html
```

To stop:

```bash
docker compose down
```

### Database persistence (Docker)

This project uses a named Docker volume for PostgreSQL data, so the database **persists across restarts**.

- Stop containers (keeps DB data):

```bash
docker compose down
```

- Stop containers **and wipe DB data** (fresh database next start):

```bash
docker compose down -v
```

Note: By default PostgreSQL is not published to `localhost:5432` (to avoid port conflicts). The API still connects to the DB over the Docker network.

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
http://localhost:8080/swagger-ui/index.html
```

### Folder structure:

```text
.
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── src
      ├── main
      │   ├── java
      │   │   └── com
      │   │       └── url
      │   │           └── shortener
      │   │               ├── ShortenerApplication.java
      │   │               ├── config
      │   │               │   └── SwaggerConfig.java
      │   │               ├── controller
      │   │               │   └── UrlController.java
      │   │               ├── dto
      │   │               │   ├── CreateShortUrlDto.java
      │   │               │   └── http
      │   │               │       ├── CreateShortUrlResponseDto.java
      │   │               │       ├── DeleteShortUrlResponseDto.java
      │   │               │       └── ErrorResponseDto.java
      │   │               ├── entity
      │   │               │   └── Url.java
      │   │               ├── exception
      │   │               │   ├── BadRequestException.java
      │   │               │   ├── DataConflictException.java
      │   │               │   ├── GlobalExceptionHandler.java
      │   │               │   └── ResourceNotFoundException.java
      │   │               ├── repository
      │   │               │   └── UrlRepository.java
      │   │               ├── scheduler
      │   │               │   └── UrlCleanupScheduler.java
      │   │               ├── service
      │   │               │   ├── IUrlService.java
      │   │               │   └── UrlServiceImpl.java
      │   │               ├── transfomer
      │   │               │   └── UrlTransformer.java
      │   │               ├── util
      │   │               │   └── CodeGenerator.java
      │   │               └── validator
      │   │                   └── UrlIdValidator.java
      │   └── resources
      │       ├── application.yml
      │       ├── application-test.yml
      │       └── db
      │           └── migration
      │               ├── V1__create_urls_table.sql
      │               └── V2__add_unique_constraint_and_indexes.sql
      └── test
            └── java
                  └── com
                        └── url
                              └── shortener
                                    ├── ShortenerApplicationTests.java
                                    └── UrlControllerIntegrationTest.java
```
