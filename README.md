# Showscape

Showscape is a modern, full-featured web application for browsing and booking tickets for movies, concerts, and other live events, inspired by platforms like BookMyShow. It is designed using a cloud-native, microservices architecture to ensure scalability, resilience, and maintainability.

This project serves as a practical example of building a complex, distributed system using modern technologies and industry best practices.

## Core Features (Planned)

*   **User Authentication:** Secure user registration and login.
*   **Movie & Event Listings:** A comprehensive catalog of movies and events with detailed information.
*   **Seat Selection & Booking:** An interactive booking flow for selecting seats and making reservations.
*   **Notifications:** Real-time updates on booking status.
*   **Personalized Recommendations:** Suggestions for users based on their interests.

## Technology Stack

*   **Architecture:** Microservices
*   **Backend:**
    *   **Language/Framework:** Java 21 & Spring Boot 3
    *   **Build Tool:** Gradle
    *   **Database:** PostgreSQL
    *   **Cache:** Redis
    *   **Message Broker:** Apache Kafka
*   **Frontend:**
    *   **Library:** React
*   **Infrastructure:**
    *   Docker & Docker Compose

## Getting Started

To run the development environment, ensure you have Docker and Docker Compose installed, then run:

```bash
docker-compose up -d
```

## Troubleshooting

### PostgreSQL Port Conflict

If you encounter `FATAL: role "admin" does not exist` or `Connection refused` errors when starting a Spring Boot service, it's likely due to another PostgreSQL instance running on your machine and occupying port `5432`.

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

---
*This README is a living document and will be updated as the project evolves.*
