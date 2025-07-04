# Movie Service

This service is responsible for managing movie data within the Showscape application. It provides RESTful APIs for CRUD operations on movies and supports querying movies by genre and release year.

## Technology Stack

*   **Language/Framework:** Java 21 & Spring Boot 3
*   **Build Tool:** Gradle
*   **Database:** PostgreSQL
*   **Cache:** Redis (planned for future integration)
*   **Message Broker:** Apache Kafka (planned for future integration)

## Getting Started

To run the Movie Service locally, ensure you have Docker and Docker Compose installed, and that the core infrastructure (PostgreSQL, Redis, Kafka) is running via the root `docker-compose.yml`.

### Prerequisites

*   Java 21 Development Kit (JDK)
*   Gradle 8.x
*   Docker Desktop (for PostgreSQL, Redis, Kafka)

### Running the Service

1.  **Start Infrastructure:** From the root of the `Showscape` project, run:
    ```bash
    docker-compose up -d
    ```

2.  **Configure Environment Variables:** Create a `.env` file in the root of the `Showscape` project with your PostgreSQL credentials:
    ```
    POSTGRES_USER=admin
    POSTGRES_PASSWORD=password
    ```
    Ensure your IDE (e.g., IntelliJ IDEA) is configured to load these environment variables for the run configuration.

3.  **Build and Run:** Navigate to the `movie-service` directory and run:
    ```bash
    ./gradlew bootRun
    ```
    The service will start on port `8080` by default.

### API Endpoints

(To be detailed as API evolves)

*   `POST /api/movies`
*   `GET /api/movies/{id}`
*   `GET /api/movies`
*   `PUT /api/movies/{id}`
*   `DELETE /api/movies/{id}`
*   `GET /api/movies/genre/{genre}`
*   `GET /api/movies/year/{year}`

## Testing

To run all tests for the Movie Service:

```bash
./gradlew clean build
```

To generate JaCoCo code coverage report:

```bash
./gradlew jacocoTestReport
```

The HTML report will be available at `build/reports/jacocoHtml/index.html`.

## Troubleshooting

### PostgreSQL Port Conflict

If you encounter `FATAL: role "admin" does not exist` or `Connection refused` errors when starting the service, it's likely due to another PostgreSQL instance running on your machine and occupying port `5432`.

To resolve this:

1.  **Identify the conflicting process:**
    ```bash
    sudo lsof -i :5432
    ```
    Look for a `postgres` process that is *not* part of your Docker containers (e.g., not `com.docke`). Note its PID.

2.  **Stop the conflicting service:**
    If it's a Homebrew-installed PostgreSQL, stop it using `brew services` (e.g., `brew services stop postgresql@17`). If it's a different process, you might need to `kill -9 <PID>` it, but be aware it might restart if managed by a system service.

3.  **Ensure Docker containers are clean and restarted:**
    ```bash
    docker-compose down
    docker volume rm Showscape_postgres_data # Only if you want a fresh database
    docker-compose up -d
    ```
