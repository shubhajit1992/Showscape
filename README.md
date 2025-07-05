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
*   **Backend:** Java 21, Spring Boot 3, Gradle, PostgreSQL, Redis, Kafka
*   **Frontend:** React
*   **Infrastructure:** Docker & Docker Compose

## Getting Started

To run the entire development environment, ensure you have Docker and Docker Compose installed, then run:

```bash
docker-compose up -d
```

## Microservices

*   **Movie Service:** Manages movie data and provides REST APIs. See its [README.md](movie-service/README.md) for details.
*   **Showscape UI (Frontend):** The React-based user interface. See its [README.md](showscapeui/README.md) for details.

---
*This README is a living document and will be updated as the project evolves.*
