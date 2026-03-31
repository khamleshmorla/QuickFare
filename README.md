# QuickFare — Fare Comparison Platform

Enterprise-grade ride fare comparison backend built with **Java 17** and **Spring Boot 3.2.4**.

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Language |
| Spring Boot 3.2.4 | Framework |
| Spring Data JPA | Data access |
| MySQL 8 | Database |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

## Project Structure

```
quickfare-backend/
├── src/main/java/com/quickfare/
│   ├── QuickfareApplication.java        # Entry point
│   ├── config/                          # Configuration beans
│   │   ├── RestTemplateConfig.java      # HTTP client timeouts
│   │   └── WebConfig.java              # CORS + static resources
│   ├── constants/                       # Application constants
│   │   └── AppConstants.java
│   ├── controller/                      # REST API endpoints
│   │   ├── FareController.java          # POST /api/fare-estimate
│   │   ├── FareContributionController.java  # CRUD /api/fares
│   │   ├── HealthController.java        # GET /api/health
│   │   └── RouteController.java         # GET /api/route
│   ├── domain/entity/                   # JPA entities
│   │   └── Fare.java
│   ├── dto/                             # Request/Response objects
│   │   ├── ApiErrorResponse.java
│   │   ├── FareEstimateRequest.java
│   │   ├── FareEstimateResponse.java
│   │   ├── FarePriceDto.java
│   │   └── RouteResponse.java
│   ├── exception/                       # Exception handling
│   │   ├── ExternalApiException.java
│   │   ├── GlobalExceptionHandler.java
│   │   ├── InvalidInputException.java
│   │   └── ResourceNotFoundException.java
│   ├── repository/                      # Data access layer
│   │   └── FareRepository.java
│   └── service/                         # Business logic
│       ├── FareCalculationService.java
│       ├── FareEstimationOrchestrator.java
│       ├── GeocodingService.java
│       ├── RouteService.java
│       └── TrafficService.java
├── src/main/resources/
│   ├── application.yml                  # Base config
│   ├── application-dev.yml              # Dev profile
│   ├── application-prod.yml             # Production profile
│   ├── banner.txt                       # Startup banner
│   ├── logback-spring.xml               # Logging configuration
│   └── static/                          # Frontend files
└── pom.xml
```

## Quick Start

### Prerequisites
- Java 17+
- MySQL 8+ running on `localhost:3306`
- Maven 3.9+ (or use included wrapper)

### Setup
```bash
# Clone and navigate
cd quickfare-backend

# Configure database (edit if needed)
# src/main/resources/application.yml → spring.datasource.*

# Build
./mvnw clean package -DskipTests

# Run (dev profile)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/health` | Health check |
| `POST` | `/api/fare-estimate` | Compare fares across providers |
| `GET` | `/api/route` | Route + fare from addresses |
| `POST` | `/api/fares` | Submit a fare contribution |
| `GET` | `/api/fares` | List all fare contributions |

### Example Request
```bash
curl -X POST http://localhost:5000/api/fare-estimate \
  -H "Content-Type: application/json" \
  -d '{
    "start_latitude": 19.0760,
    "start_longitude": 72.8777,
    "end_latitude": 19.1136,
    "end_longitude": 72.8697
  }'
```

## Architecture

- **Clean Architecture** — Controller → Service → Repository → Entity
- **SOLID Principles** — No business logic in controllers
- **Constructor Injection** — No field injection
- **Global Exception Handling** — `@ControllerAdvice` with standardized error responses
- **Externalized Configuration** — All values in `application.yml`
- **Profile-based Config** — `dev` and `prod` profiles

## License

MIT
