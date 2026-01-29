# 🔗 URL Shortener Service

A production-ready **URL Shortener microservice** built with Spring Boot 3.3.4 and Java 21. This service allows users to create short URLs, redirect to original URLs, and manage URL lifecycle with optional TTL (time-to-live) expiration.

---

## ✨ Features

- **Create Short URLs** - Generate 6-character alphanumeric short codes (62^6 = 56+ billion combinations)
- **Custom Short Codes** - Optionally provide your own short URL ID
- **TTL Support** - Set expiration time in seconds; URLs without TTL persist indefinitely
- **Auto-Cleanup** - Background scheduler removes expired URLs automatically
- **RESTful API** - Clean API design with proper HTTP status codes
- **Swagger Documentation** - Interactive API documentation via OpenAPI 3.0
- **Docker Ready** - Multi-stage Docker build with Docker Compose orchestration
- **Database Migrations** - Versioned schema with Flyway
- **Comprehensive Testing** - Integration tests with H2 in-memory database

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 21** | Programming language |
| **Spring Boot 3.3.4** | Application framework |
| **Spring Data JPA** | Database ORM |
| **PostgreSQL** | Production database |
| **H2 Database** | Testing database |
| **Flyway** | Database migrations |
| **Lombok** | Boilerplate reduction |
| **SpringDoc OpenAPI** | Swagger UI documentation |
| **Docker & Docker Compose** | Containerization |
| **Maven** | Build tool |

---

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker & Docker Compose** (recommended)

---

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/i-am-sushant/url-shortener-springboot.git
cd url-shortener-springboot

# Start the application with PostgreSQL
docker compose up --build

# The API is available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

### Option 2: Local Development

```bash
# Ensure PostgreSQL is running locally on port 5432
# Database: shortener_db, User: postgres, Password: root

# Build and run
mvn clean install
mvn spring-boot:run
```

---

## 📡 API Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/api/v1/create` | Create a shortened URL | JSON (see below) |
| `GET` | `/api/v1/{urlId}` | Redirect to original URL | - |
| `DELETE` | `/api/v1/delete/{urlId}` | Delete a shortened URL | - |

### Create Short URL Request

```json
{
  "originalUrl": "https://www.example.com",
  "urlId": "abc123",    // Optional: 6 alphanumeric characters
  "ttl": 3600           // Optional: Time-to-live in seconds
}
```

### Response Examples

**Success (201 Created):**
```json
{
  "id": 1,
  "originalUrl": "https://www.example.com",
  "ttl": 3600,
  "urlId": "abc123"
}
```

**Error (409 Conflict):**
```json
{
  "statusCode": 409,
  "errorMessage": "URL ID Already exist."
}
```

---

## 🗄️ Database Persistence

This project uses a named Docker volume for PostgreSQL data.

```bash
# Stop containers (keeps database data)
docker compose down

# Stop containers AND wipe database (fresh start)
docker compose down -v
```

> **Note:** PostgreSQL is not exposed to localhost:5432 by default to avoid port conflicts. The API connects via the Docker network.

---

## 🧪 Running Tests

```bash
mvn test
```

Tests use the **H2 in-memory database** configured in `application-test.yml` with Flyway disabled.

---

## 📖 Swagger Documentation

Access interactive API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER                            │
│                     (UrlController.java)                        │
│   • Handles HTTP requests (POST, GET, DELETE)                   │
│   • Input validation using @Valid                               │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                              │
│                    (UrlServiceImpl.java)                        │
│   • Business logic & short code generation                      │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                             │
│                    (UrlRepository.java)                         │
│   • Spring Data JPA repository                                  │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   DATABASE (PostgreSQL)                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
.
├── docker-compose.yml          # Docker orchestration
├── Dockerfile                  # Multi-stage Docker build
├── pom.xml                     # Maven dependencies
└── src
    ├── main
    │   ├── java/com/url/shortener
    │   │   ├── ShortenerApplication.java    # Main application class
    │   │   ├── config/
    │   │   │   └── SwaggerConfig.java       # OpenAPI configuration
    │   │   ├── controller/
    │   │   │   └── UrlController.java       # REST endpoints
    │   │   ├── dto/
    │   │   │   ├── CreateShortUrlDto.java   # Request DTO
    │   │   │   └── http/                    # Response DTOs
    │   │   ├── entity/
    │   │   │   └── Url.java                 # JPA entity
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   ├── BadRequestException.java
    │   │   │   ├── DataConflictException.java
    │   │   │   └── ResourceNotFoundException.java
    │   │   ├── repository/
    │   │   │   └── UrlRepository.java       # Data access layer
    │   │   ├── scheduler/
    │   │   │   └── UrlCleanupScheduler.java # TTL cleanup job
    │   │   ├── service/
    │   │   │   ├── IUrlService.java         # Service interface
    │   │   │   └── UrlServiceImpl.java      # Business logic
    │   │   ├── transformer/
    │   │   │   └── UrlTransformer.java      # DTO-Entity mapper
    │   │   ├── util/
    │   │   │   └── CodeGenerator.java       # Short code generator
    │   │   └── validator/
    │   │       └── UrlIdValidator.java      # Custom validation
    │   └── resources/
    │       ├── application.yml              # Production config
    │       ├── application-test.yml         # Test config
    │       └── db/migration/                # Flyway migrations
    └── test/
        └── java/com/url/shortener/          # Integration tests
```

---

## ⚙️ Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8080 | Application port |
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://localhost:5432/shortener_db | Database URL |
| `SPRING_DATASOURCE_USERNAME` | postgres | Database username |
| `SPRING_DATASOURCE_PASSWORD` | root | Database password |
| `SCHEDULER_CLEANUP_INTERVAL_MS` | 60000 | Expired URL cleanup interval (ms) |

---

## 🔑 Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **6-character alphanumeric codes** | Provides 56B+ combinations; short and human-readable |
| **Optional TTL with null = forever** | Flexible; users choose URL persistence |
| **Background scheduler for cleanup** | Efficient; avoids checking expiry on every request |
| **Flyway for migrations** | Production-safe, versioned schema changes |
| **Interface + Implementation pattern** | Enables mocking and future implementations |
| **Multi-stage Docker build** | Smaller final image; separates build from runtime |

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Sushant**

- GitHub: [@i-am-sushant](https://github.com/i-am-sushant)
