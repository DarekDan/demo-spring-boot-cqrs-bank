# Architecture

## Overview
The system is a reactive, event-driven CQRS service for basic banking operations. It separates write (command) paths from read (query) paths, persists data via R2DBC, and uses RabbitMQ for asynchronous domain event propagation. Resilience4j guards integrations with circuit breakers.

## Components
- API Layer (WebFlux)
  - Command Controllers: accept write operations; return Mono results.
  - Query Controllers: serve read endpoints; return Mono/Flux DTOs.
- Application Layer
  - Command Handlers: orchestrate domain operations, validation, persistence, and event publication.
  - Query Handlers: execute read-optimized queries.
- Domain Layer
  - Aggregates/Entities: Account, Transaction/Operation
  - Value Objects: Money, AccountId, etc.
  - Domain Events: AccountCreated, MoneyDeposited, MoneyWithdrawn, TransferInitiated/Completed/Failed
- Infrastructure
  - Persistence: Spring Data R2DBC repositories and custom queries
  - Messaging: Spring AMQP (RabbitMQ) producers/consumers
  - Resilience: Resilience4j for outbound integrations
  - Observability: Spring Boot Actuator, structured logging

## Data Flow

Write (Command) Path:
1) HTTP request -> Command Controller
2) Validation -> Command Handler
3) Domain logic -> R2DBC persistence
4) Publish domain event -> RabbitMQ
5) Return reactive response

Read (Query) Path:
1) HTTP request -> Query Controller
2) Query Handler -> R2DBC read model
3) Stream or return DTOs via Mono/Flux

Messaging:
- Producers publish domain events (e.g., after successful persistence)
- Consumers may update read projections or trigger side effects
- At-least-once semantics recommended with idempotent handlers

## Persistence Strategy
- Non-blocking R2DBC for PostgreSQL
- Schema typically includes:
  - accounts (id, owner, balance, status, version)
  - operations/transactions (id, account_id, type, amount, timestamp, metadata)
- Optimistic concurrency recommended using a version column for aggregates
- Read projections can use denormalized tables/views for fast queries

## CQRS Guidelines
- Keep command and query models separate
- Commands encapsulate intent; queries return data without side effects
- Domain events emitted after state changes; consumers handle projection updates

## Error Handling
- Validation errors -> 400 Bad Request
- Business rule violations (e.g., insufficient funds) -> 409 Conflict
- Not found -> 404
- Unexpected -> 500 with correlation id
- Reactive error mapping via onErrorResume / ControllerAdvice

## Resilience Patterns
- Circuit Breaker around messaging and external calls
- Timeouts for I/O operations
- Retries with backoff where idempotency is ensured
- Bulkhead/semaphore isolation for high-latency paths

## Security Considerations
- Add authN/authZ (e.g., Spring Security) for production
- Input validation and output encoding
- Protect from replay/duplication by idempotent command handling

## Testing Strategy
- Unit tests: domain and handlers
- WebFlux slice tests for controllers
- Integration tests: R2DBC repositories
- Messaging tests with RabbitMQ Testcontainers
- End-to-end scenarios with Testcontainers network

## Deployment
- Containerized Spring Boot app
- External dependencies: PostgreSQL, RabbitMQ
- Health checks via Actuator
- Config via environment variables or config server
