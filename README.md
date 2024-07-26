# Link Shortener

Link Shortener is a Spring Boot application built with Kotlin that allows you to shorten URLs. It provides an API for
shortening long URLs into more manageable and shareable links. Additionally, it allows you to customize short URLs and
ensures that the provided URLs are valid and accessible.

## Features

- Shorten long URLs into unique, short links.
- Redirect to original URLs using short links.
- Customize short URLs to make them more meaningful.
- Validates URLs to ensure they exist and return successful responses (not 4xx or 5xx).

## Technologies Used

- Spring Boot: Framework for building robust Java/Kotlin applications.
- Spring Data JPA: Simplifies the implementation of data access layers.
- Postgres: Database for storing shortened URLs and their corresponding original URLs.
- Liquibase: Database schema migration tool for managing database changes over time.
- Testcontainers: Library for spinning up temporary Docker containers during testing.
- JUnit 5: Testing framework for unit and integration tests.
- Swagger: API documentation tool for easy API exploration and testing.
- Gradle: Build automation tool for managing project dependencies and tasks.

## Prerequisites

- Java 21: Ensure you have Java 21 installed on your system.
- Docker Compose: If you plan to use Docker Compose for running the application and database.

## Running the Application

To run the application, follow these steps:

```
git clone https://github.com/artmkrvshn/link-shortener.git
cd link-shortener
docker compose up
```

Once the application is running, you can access the API documentation via Swagger UI by navigating
to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your web browser.

## Testing

The project has a test coverage of 85%. The tests include unit tests, integration tests, and endpoint tests to ensure
the functionality and reliability of the application.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
