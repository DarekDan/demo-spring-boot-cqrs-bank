# Demo Spring Boot CQRS Bank

A reactive, event-driven demo banking service showcasing CQRS with Spring Boot WebFlux, R2DBC (PostgreSQL), RabbitMQ messaging, and Resilience4j circuit breaking. It is designed as a reference implementation for clean separation of write/read concerns, reactive I/O, and testability with Testcontainers.

## Features
- Reactive HTTP APIs (Spring WebFlux)
- CQRS: separated command (write) and query (read) flows
- R2DBC PostgreSQL for non-blocking persistence
- RabbitMQ for asynchronous event publishing/consumption
- Resilience4j Circuit Breakers for downstream resilience
- Actuator for health/metrics
- Testcontainers-based integration tests

## Tech Stack
- Java 21 (compatible), Spring Boot 3.5.x
- WebFlux, Spring Data R2DBC, Spring AMQP
- PostgreSQL (r2dbc-postgresql), RabbitMQ
- Resilience4j CircuitBreaker (reactor)
- Lombok
- Test: Spring Boot Test, Reactor Test, Testcontainers

## Getting Started
1) Prerequisites:
- JDK 21+
- Docker (for Testcontainers or local infra)
- Maven 3.9+

2) Configuration:
- Application properties should define:
  - R2DBC URL, user, password
  - JDBC URL (if flyway/liquibase or blocking admin tooling used)
  - RabbitMQ host/port/credentials
  - Optional: Resilience4j circuit breaker configs
- Typical R2DBC URL: r2dbc:postgresql://localhost:5432/bank
- Typical Rabbit: amqp://guest:guest@localhost:5672

3) Running locally:
- Start PostgreSQL and RabbitMQ locally or via docker-compose
- mvn spring-boot:run

4) Running tests:
- mvn test
- Tests use Testcontainers for PostgreSQL/RabbitMQ automatically

## Typical API Flow
- Command API: create account, deposit, withdraw, transfer => validate and persist via R2DBC; publish domain events to RabbitMQ.
- Query API: get account details, balances, operation history => read-optimized queries via R2DBC.

## Observability
- Actuator: /actuator/health, /actuator/metrics, etc.
- Correlate logs across async boundaries; include request IDs where applicable.

## Resilience
- Circuit breakers wrap outbound calls (e.g., messaging, external services).
- Timeouts and retries should be tuned per endpoint.

## Development Notes
- Prefer reactive types (Mono/Flux) end-to-end.
- Avoid blocking calls on event loops; use bounded elastic schedulers if necessary.
- Validate request DTOs; keep controllers thin.
- Use CQRS segregation at package/module level to avoid coupling.

## License
For demo/educational purposes.
