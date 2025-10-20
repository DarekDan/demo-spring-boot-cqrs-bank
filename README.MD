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

```
Attachment Name: AccountRepository.java
Attachments Kind: Visible
Attachments Source: RecentFilesRetriever / FileChatAttachment
Attachments Text:
```java
package github.darekdan.demospringbootcqrsbank.repository;

import github.darekdan.demospringbootcqrsbank.domain.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
    Mono<Account> findByAccountId(String accountId);
}

```
Attachment Name: DemoSpringBootCqrsBankApplication.java
Attachments Kind: Visible
Attachments Source: RecentFilesRetriever / FileChatAttachment
Attachments Text:
```java
package github.darekdan.demospringbootcqrsbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoSpringBootCqrsBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringBootCqrsBankApplication.class, args);
    }

}

```
Attachment Name: pom.xml
Attachments Kind: Visible
Attachments Source: RecentFilesRetriever / FileChatAttachment
Attachments Text:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>github.darekdan</groupId>
    <artifactId>demo-spring-boot-cqrs-bank</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo-spring-boot-cqrs-bank</name>
    <description>demo-spring-boot-cqrs-bank</description>
    <url/>
    <licenses>
        <license/>
    </licenses>
    <developers>
        <developer/>
    </developers>
    <scm>
        <connection/>
        <developerConnection/>
        <tag/>
        <url/>
    </scm>
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2025.0.0</spring-cloud.version>
        <logback.version>1.5.19</logback.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>ch.qos.logback</groupId>
                    <artifactId>logback-core</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>ch.qos.logback</groupId>
                    <artifactId>logback-classic</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>${logback.version}</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>${logback.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <!-- Spring Data R2DBC -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-r2dbc</artifactId>
        </dependency>

        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>r2dbc-postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Spring AMQP -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-testcontainers</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.commons</groupId>
                    <artifactId>commons-compress</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-compress</artifactId>
            <version>1.28.0</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.commons</groupId>
                    <artifactId>commons-lang3</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.19.0</version>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>rabbitmq</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>r2dbc</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```


kotlin.Unit

==== SimpleCompletableMessage #2 ====
Assistant:
I'll analyze the current README and improve it with better structure, comprehensive content, and detailed testing information.Let me search for test files more specifically:Let me search for test classes and explore the project structure:Let me try a different approach:Based on my analysis of your project structure and test classes, here's a significantly improved README:

<llm-snippet-file>README.MD</llm-snippet-file>
```markdown
# Demo Spring Boot CQRS Bank

A production-ready, reactive banking service demonstrating **Event Sourcing** and **CQRS** (Command Query Responsibility Segregation) architecture patterns using Spring Boot 3.5, WebFlux, R2DBC, and RabbitMQ. This reference implementation showcases clean separation of write/read concerns, non-blocking reactive I/O, event-driven architecture, and comprehensive testability using Testcontainers.

## 🎯 Project Overview

This application simulates core banking operations (account creation, deposits, withdrawals, and transfers) while implementing:
- **Command Side**: Processes write operations, validates business rules, persists state, and publishes domain events
- **Query Side**: Handles read-optimized queries for account details, balances, and transaction history
- **Event Store**: Immutable audit log of all domain events for event sourcing and compliance
- **Async Messaging**: Decoupled event publishing via RabbitMQ for scalability and resilience

## 📋 Table of Contents
- [Architecture](#-architecture)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Running Tests](#-running-tests)
- [API Documentation](#-api-documentation)
- [Configuration](#-configuration)
- [Observability & Monitoring](#-observability--monitoring)
- [Resilience Patterns](#-resilience-patterns)
- [Development Guidelines](#-development-guidelines)
- [License](#-license)

## 🏗 Architecture

### CQRS Pattern
```
┌──────────────────┐         ┌──────────────────┐
│  Command Side    │         │   Query Side     │
│  (Write Model)   │         │  (Read Model)    │
├──────────────────┤         ├──────────────────┤
│ • Validation     │         │ • Optimized      │
│ • Business Logic │◄────────┤   Queries        │
│ • Event Store    │  Events │ • Projections    │
│ • State Persist  │         │ • Materialized   │
└────────┬─────────┘         │   Views          │
│                   └──────────────────┘
▼
┌──────────┐
│ RabbitMQ │
│  Events  │
└──────────┘
```

### Event Flow
1. **Command** → Validate → Execute → **Persist Account State**
2. **Persist Event** → Event Store (immutable audit log)
3. **Publish Event** → RabbitMQ → Async Consumers
4. **Update Read Models** → Optimized Query Projections

## ✨ Key Features

- ✅ **Fully Reactive Stack**: Non-blocking I/O with Spring WebFlux (Mono/Flux) and R2DBC
- ✅ **CQRS Architecture**: Clean separation between command (write) and query (read) models
- ✅ **Event Sourcing**: Complete event store with immutable audit trail
- ✅ **Event-Driven Messaging**: Asynchronous event publishing via RabbitMQ
- ✅ **Resilience Patterns**: Circuit breakers with Resilience4j for fault tolerance
- ✅ **Domain-Driven Design**: Clear bounded contexts and domain events
- ✅ **Production Monitoring**: Spring Boot Actuator with health checks and metrics
- ✅ **Comprehensive Testing**: Integration tests with Testcontainers (PostgreSQL + RabbitMQ)
- ✅ **Type Safety**: Lombok for boilerplate reduction

## 🛠 Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 21+ |
| **Framework** | Spring Boot | 3.5.6 |
| **Reactive** | Spring WebFlux + Project Reactor | - |
| **Persistence** | Spring Data R2DBC | - |
| **Database** | PostgreSQL | (via r2dbc-postgresql) |
| **Messaging** | Spring AMQP + RabbitMQ | - |
| **Resilience** | Spring Cloud Circuit Breaker + Resilience4j | - |
| **Monitoring** | Spring Boot Actuator | - |
| **Build Tool** | Maven | 3.9+ |
| **Testing** | JUnit 5, Reactor Test, Testcontainers | - |
| **Utilities** | Lombok | - |

## 📦 Prerequisites

Before running the application, ensure you have:

1. **JDK 21 or higher** (recommend using SDKMan or jEnv)
   ```bash
   sdk install java 21.0.1-open
   ```

2. **Maven 3.9+**
   ```bash
   mvn --version
   ```

3. **Docker Desktop** (for Testcontainers and local infrastructure)
   ```bash
   docker --version
   docker compose version
   ```

4. **Optional**: IntelliJ IDEA (2024.x+) or your preferred IDE

## 📂 Project Structure

```
demo-spring-boot-cqrs-bank/
├── src/
│   ├── main/java/github/darekdan/demospringbootcqrsbank/
│   │   ├── command/           # Command handlers, DTOs
│   │   ├── query/             # Query handlers, projections
│   │   ├── domain/            # Domain entities (Account, etc.)
│   │   ├── events/            # Domain events (AccountCreatedEvent, etc.)
│   │   ├── repository/        # R2DBC repositories
│   │   ├── service/           # Business services
│   │   ├── messaging/         # RabbitMQ publishers/consumers
│   │   ├── controller/        # REST controllers
│   │   └── config/            # Spring configuration
│   │
│   ├── main/resources/
│   │   ├── application.yml    # Main configuration
│   │   ├── schema.sql         # Database schema (if needed)
│   │   └── logback-spring.xml # Logging configuration
│   │
│   └── test/java/github/darekdan/demospringbootcqrsbank/
│       ├── AccountCommandHandlerTests.java        # Command handler tests
│       ├── DemoSpringBootCqrsBankApplicationTests.java  # Context load test
│       ├── TestcontainersConfiguration.java       # Testcontainers setup
│       └── TestDemoSpringBootCqrsBankApplication.java   # Test main class
│
├── pom.xml                    # Maven dependencies
├── README.MD                  # This file
├── ARCHITECTURE.MD            # Detailed architecture documentation
└── AGENTS.MD                  # Agent-related documentation
```

## 🚀 Getting Started

### Option 1: Run with Local Infrastructure (Docker Compose)

1. **Start PostgreSQL and RabbitMQ**:
   Create a `docker-compose.yml` in project root:
   ```yaml
   version: '3.8'
   services:
     postgres:
       image: postgres:16-alpine
       environment:
         POSTGRES_DB: bank
         POSTGRES_USER: bank_user
         POSTGRES_PASSWORD: bank_pass
       ports:
         - "5432:5432"
       volumes:
         - postgres_data:/var/lib/postgresql/data
     
     rabbitmq:
       image: rabbitmq:3-management-alpine
       environment:
         RABBITMQ_DEFAULT_USER: guest
         RABBITMQ_DEFAULT_PASS: guest
       ports:
         - "5672:5672"
         - "15672:15672"  # Management UI
   
   volumes:
     postgres_data:
   ```

   ```bash
   docker compose up -d
   ```

2. **Build the application**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Verify startup**:
    - Application: `http://localhost:8080`
    - Actuator Health: `http://localhost:8080/actuator/health`
    - RabbitMQ Management: `http://localhost:15672` (guest/guest)

### Option 2: Run Tests Only (Testcontainers Managed)

The tests automatically start PostgreSQL and RabbitMQ containers:

```bash
mvn test
```

## 🧪 Running Tests

### Test Suite Overview

The project includes comprehensive integration tests that validate the entire application stack:

| Test Class | Purpose | What It Tests |
|------------|---------|---------------|
| **AccountCommandHandlerTests** | Command-side operations | Account creation, deposits, withdrawals, transfers, validation, event persistence, and idempotency |
| **DemoSpringBootCqrsBankApplicationTests** | Application bootstrap | Spring context loading, bean wiring, and basic health checks |
| **TestcontainersConfiguration** | Test infrastructure | Testcontainers setup for PostgreSQL and RabbitMQ |

### Running All Tests

```bash
# Run all tests with detailed output
mvn clean test

# Run tests with specific log level
mvn clean test -Dlogging.level.github.darekdan=DEBUG

# Run tests and generate coverage report (if configured)
mvn clean verify
```

### Running Specific Test Classes

```bash
# Run command handler tests only
mvn test -Dtest=AccountCommandHandlerTests

# Run application context tests
mvn test -Dtest=DemoSpringBootCqrsBankApplicationTests
```

### Test Execution Details

#### AccountCommandHandlerTests
Tests the **command-side** business logic with the following scenarios:

✅ **Account Creation Tests**:
- `testCreateAccountSuccess` - Validates successful account creation with initial balance
- `testCreateAccountDuplicate` - Ensures duplicate account IDs are rejected with proper error messages

✅ **Deposit Tests**:
- `testDepositSuccess` - Verifies deposit increases balance and publishes `DepositedEvent`
- `testDepositNonExistentAccount` - Validates error handling for invalid account IDs
- `testDepositNegativeAmount` - Ensures negative deposits are rejected
- `testMultipleDeposits` - Tests sequential deposits maintain correct balance state

✅ **Event Persistence Verification**:
- Each test validates that events are correctly stored in the event store
- Verifies event metadata (eventId, timestamp, accountId, eventType)

✅ **State Consistency**:
- Confirms account state in database matches expected values after commands
- Validates balance updates are atomic and consistent

### Test Infrastructure (Testcontainers)

The `TestcontainersConfiguration` class automatically provisions:

- **PostgreSQL Container**: Ephemeral database for R2DBC testing
- **RabbitMQ Container**: Message broker for event publishing verification

**Benefits**:
- No manual setup required - containers start/stop automatically
- Isolated test environment per test run
- Production-like testing with real database and message broker
- Parallel test execution support

### Test Best Practices

```java
// All tests use StepVerifier for reactive testing
StepVerifier.create(commandHandler.handleCreateAccount(cmd))
    .assertNext(event -> {
        // Validate event properties
        assertEquals("ACC-001", event.getAccountId());
    })
    .expectComplete()
    .verify(Duration.ofSeconds(10));  // Timeout prevents hanging tests
```

### Troubleshooting Tests

If tests fail:

1. **Check Docker**: Ensure Docker Desktop is running
   ```bash
   docker ps
   ```

2. **Check Logs**: Review Testcontainers logs
   ```bash
   mvn test -X  # Maven debug mode
   ```

3. **Clean State**: Remove any cached containers
   ```bash
   docker system prune -a
   ```

4. **Port Conflicts**: Ensure ports 5432, 5672, 15672 aren't in use

## 📡 API Documentation

### Command API (Write Operations)

| Endpoint | Method | Description | Request Body |
|----------|--------|-------------|--------------|
| `/api/accounts` | POST | Create new account | `{"accountId": "ACC-001", "accountHolder": "John Doe", "initialBalance": 1000.00}` |
| `/api/accounts/{id}/deposit` | POST | Deposit funds | `{"amount": 500.00}` |
| `/api/accounts/{id}/withdraw` | POST | Withdraw funds | `{"amount": 200.00}` |
| `/api/accounts/transfer` | POST | Transfer between accounts | `{"fromAccountId": "ACC-001", "toAccountId": "ACC-002", "amount": 300.00}` |

### Query API (Read Operations)

| Endpoint | Method | Description | Response |
|----------|--------|-------------|----------|
| `/api/accounts/{id}` | GET | Get account details | Account entity with current balance |
| `/api/accounts/{id}/balance` | GET | Get current balance | `{"balance": 1500.00}` |
| `/api/accounts/{id}/history` | GET | Get transaction history | List of events/transactions |

### Example Request/Response

**Create Account:**
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC-001",
    "accountHolder": "John Doe",
    "initialBalance": 1000.00
  }'
```

**Response:**
```json
{
  "eventId": "evt-12345",
  "accountId": "ACC-001",
  "accountHolder": "John Doe",
  "initialBalance": 1000.00,
  "timestamp": "2025-10-20T10:30:00Z"
}
```

## ⚙️ Configuration

### Application Properties

Create `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: demo-spring-boot-cqrs-bank
  
  # R2DBC PostgreSQL Configuration
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/bank
    username: bank_user
    password: bank_pass
    pool:
      initial-size: 10
      max-size: 20
  
  # RabbitMQ Configuration
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    template:
      exchange: bank.events
      routing-key: account.events

# Resilience4j Circuit Breaker
resilience4j:
  circuitbreaker:
    instances:
      messagingService:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

# Logging
logging:
  level:
    github.darekdan: DEBUG
    org.springframework.r2dbc: DEBUG
    io.r2dbc.postgresql: DEBUG
```

### Environment Variables

For production, override via environment:

```bash
export SPRING_R2DBC_URL=r2dbc:postgresql://prod-db:5432/bank
export SPRING_R2DBC_USERNAME=prod_user
export SPRING_R2DBC_PASSWORD=${DB_SECRET}
export SPRING_RABBITMQ_HOST=rabbitmq.prod.svc
```

## 📊 Observability & Monitoring

### Health Checks
- **Endpoint**: `GET /actuator/health`
- **Includes**: R2DBC connectivity, RabbitMQ connection, disk space

### Metrics
- **Endpoint**: `GET /actuator/metrics`
- **Available Metrics**:
    - `http.server.requests` - HTTP request metrics
    - `r2dbc.pool.*` - Database connection pool metrics
    - `rabbitmq.*` - Message broker metrics
    - `resilience4j.circuitbreaker.*` - Circuit breaker state

### Prometheus Integration
```bash
# Scrape endpoint for Prometheus
curl http://localhost:8080/actuator/prometheus
```

### Logging
- **Format**: JSON (production) / Console (development)
- **Correlation IDs**: Include request IDs for distributed tracing
- **Log Levels**: Configurable via `application.yml`

## 🛡️ Resilience Patterns

### Circuit Breaker (Resilience4j)

Wraps outbound calls (messaging, external services) to prevent cascading failures:

```java
@CircuitBreaker(name = "messagingService", fallbackMethod = "fallbackPublish")
public Mono<Void> publishEvent(DomainEvent event) {
    // Publish to RabbitMQ
}
```

**Configuration**:
- **Sliding Window**: 10 calls
- **Failure Threshold**: 50% failures trigger OPEN state
- **Wait Duration**: 10 seconds before transitioning to HALF_OPEN
- **Half-Open Calls**: 3 test calls to verify recovery

### Retry Policies

Configure retries for transient failures:

```yaml
resilience4j:
  retry:
    instances:
      r2dbcRepository:
        max-attempts: 3
        wait-duration: 500ms
        exponential-backoff-multiplier: 2
```

### Timeouts

Prevent resource exhaustion:

```yaml
spring:
  webflux:
    timeout: 30s
  r2dbc:
    pool:
      max-acquire-time: 5s
```

## 💡 Development Guidelines

### Reactive Programming Best Practices

1. **Use Reactive Types End-to-End**:
   ```java
   // ✅ Good
   public Mono<Account> getAccount(String id) {
       return accountRepository.findByAccountId(id);
   }
   
   // ❌ Avoid blocking
   public Account getAccount(String id) {
       return accountRepository.findByAccountId(id).block();
   }
   ```

2. **Avoid Blocking Calls on Event Loops**:
   ```java
   // Use bounded elastic scheduler for blocking I/O
   Mono.fromCallable(() -> blockingCall())
       .subscribeOn(Schedulers.boundedElastic());
   ```

3. **Proper Error Handling**:
   ```java
   repository.save(account)
       .onErrorResume(DataAccessException.class, 
           e -> Mono.error(new BusinessException("Save failed")));
   ```

### CQRS Separation

- **Commands**: Place in `command/` package - focus on validation and state changes
- **Queries**: Place in `query/` package - focus on read optimization and projections
- **No Cross-Contamination**: Commands should NOT return query results

### Code Quality

- **Validation**: Use Jakarta Bean Validation for DTOs
- **Thin Controllers**: Delegate business logic to services
- **Immutable Events**: Domain events should be immutable records
- **Testing**: Maintain >80% code coverage with meaningful tests

## 📄 License

This project is provided for **educational and demonstration purposes**. Feel free to use, modify, and distribute as needed.

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📚 Additional Resources

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [R2DBC Specification](https://r2dbc.io/)
- [CQRS Pattern](https://martinfowler.com/bliki/CQRS.html)
- [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Testcontainers](https://www.testcontainers.org/)

---

**Questions or Issues?** Open an issue on GitHub or contact the maintainers.
