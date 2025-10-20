# Instructions for Coding Agents (AI Assistants)

This document provides comprehensive guidance for AI coding agents working on this CQRS banking application. The primary methodology is **Test-Driven Development (TDD)** with reactive programming paradigms.

## Core Principles

### 1. Test-Driven Development (TDD)
**Always follow the Red-Green-Refactor cycle:**
1. **RED**: Write a failing test that describes the desired behavior
2. **GREEN**: Write minimal code to make the test pass
3. **REFACTOR**: Improve code quality while keeping tests green

**Never** write production code without a failing test first, except for:
- Pure data classes (DTOs, value objects with no logic)
- Configuration classes with zero business logic
- Trivial getters/setters

### 2. Reactive Programming Constraints
- **Never block**: Avoid `.block()`, `.blockFirst()`, `.blockLast()` in production code
- Use `StepVerifier` for testing reactive streams
- Propagate reactive types (`Mono<T>`, `Flux<T>`) throughout the entire call chain
- Use `.subscribeOn()` and `.publishOn()` explicitly when changing execution contexts
- Handle errors reactively with `.onErrorResume()`, `.onErrorReturn()`, `.onErrorMap()`

### 3. CQRS Separation
- **Commands** (write side): located in `command` package, return `Mono<Void>` or `Mono<ResultDTO>`
- **Queries** (read side): located in `query` package, return `Mono<T>` or `Flux<T>`
- **No mixing**: Commands must not return domain state; queries must not mutate state
- Domain events bridge the two sides via RabbitMQ

## TDD Workflow by Layer

### Domain Layer (Core Business Logic)

**Order of test creation:**
1. Value Objects tests (Money, AccountId, etc.)
2. Aggregate/Entity tests (Account, Transaction)
3. Domain Event tests
4. Domain Service tests (if any)

**Example TDD cycle for a new aggregate method:**
```java
// STEP 1: RED - Write failing test
@Test
void shouldDebitAccountWhenSufficientFunds() {
    Account account = Account.create("ACC123", "John Doe", Money.of(100));
    
    Mono<Account> result = account.debit(Money.of(30));
    
    StepVerifier.create(result)
        .assertNext(acc -> {
            assertThat(acc.getBalance()).isEqualTo(Money.of(70));
            assertThat(acc.getDomainEvents()).hasSize(1);
            assertThat(acc.getDomainEvents().get(0)).isInstanceOf(MoneyWithdrawn.class);
        })
        .verifyComplete();
}

// STEP 2: GREEN - Implement minimal code
public Mono<Account> debit(Money amount) {
    if (balance.isLessThan(amount)) {
        return Mono.error(new InsufficientFundsException());
    }
    this.balance = balance.subtract(amount);
    this.addDomainEvent(new MoneyWithdrawn(this.id, amount, Instant.now()));
    return Mono.just(this);
}

// STEP 3: REFACTOR - Extract validation, improve naming, etc.
```

**Domain layer test requirements:**
- Pure unit tests, no Spring context, no I/O
- Test invariants and business rules exhaustively
- Test edge cases: boundary values, null handling, empty collections
- Test domain event emission for state changes

### Application Layer (Handlers)

**Command Handler TDD:**
1. Write test with mocked repository and event publisher
2. Verify interaction with repository (save called)
3. Verify event publication with correct payload
4. Verify error scenarios (validation, business rule violations)

**Example:**
```java
@ExtendWith(MockitoExtension.class)
class CreateAccountCommandHandlerTest {
    
    @Mock
    private AccountRepository repository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private CreateAccountCommandHandler handler;
    
    @Test
    void shouldCreateAccountAndPublishEvent() {
        // RED: Write test first
        CreateAccountCommand command = new CreateAccountCommand("ACC123", "John", Money.of(100));
        Account savedAccount = Account.create(command.accountId(), command.owner(), command.initialDeposit());
        
        when(repository.save(any(Account.class))).thenReturn(Mono.just(savedAccount));
        when(eventPublisher.publish(any(DomainEvent.class))).thenReturn(Mono.empty());
        
        Mono<AccountCreatedResult> result = handler.handle(command);
        
        StepVerifier.create(result)
            .assertNext(r -> assertThat(r.accountId()).isEqualTo("ACC123"))
            .verifyComplete();
            
        verify(repository).save(any(Account.class));
        verify(eventPublisher).publish(argThat(event -> 
            event instanceof AccountCreated && 
            ((AccountCreated) event).accountId().equals("ACC123")
        ));
    }
    
    // GREEN: Implement handler.handle() method
    // REFACTOR: Extract validation, improve error handling
}
```

**Query Handler TDD:**
1. Write test with mocked repository
2. Verify correct repository method called with correct parameters
3. Verify DTO mapping
4. Test empty results, pagination, filtering

### Infrastructure Layer

**Repository Tests:**
- Use `@DataR2dbcTest` or Testcontainers for real PostgreSQL
- Test CRUD operations
- Test custom queries with complex joins/aggregations
- Test concurrent access and optimistic locking
- Verify reactive streams behavior (no premature evaluation)

**Example:**
```java
@DataR2dbcTest
@Testcontainers
class AccountRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Autowired
    private AccountRepository repository;
    
    @Test
    void shouldSaveAndRetrieveAccount() {
        // RED
        Account account = Account.create("ACC123", "John", Money.of(100));
        
        Mono<Account> saveAndFind = repository.save(account)
            .flatMap(saved -> repository.findById(saved.getId()));
        
        StepVerifier.create(saveAndFind)
            .assertNext(found -> {
                assertThat(found.getId()).isEqualTo("ACC123");
                assertThat(found.getBalance()).isEqualTo(Money.of(100));
            })
            .verifyComplete();
        
        // GREEN: Implement repository if needed
        // REFACTOR: Add indexes, optimize queries
    }
}
```

**Messaging Tests:**
- Use RabbitMQ Testcontainers
- Test event serialization/deserialization
- Test routing keys and exchange bindings
- Test idempotent consumers
- Test DLQ scenarios

### API Layer (Controllers)

**Controller Tests:**
- Use `@WebFluxTest` for slice tests
- Mock handler layer
- Test HTTP status codes
- Test request/response mapping
- Test validation errors
- Test content negotiation

**Example:**
```java
@WebFluxTest(AccountCommandController.class)
class AccountCommandControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private CreateAccountCommandHandler handler;
    
    @Test
    void shouldReturn201WhenAccountCreated() {
        // RED
        CreateAccountRequest request = new CreateAccountRequest("John", 100.0);
        AccountCreatedResult result = new AccountCreatedResult("ACC123");
        
        when(handler.handle(any())).thenReturn(Mono.just(result));
        
        webTestClient.post()
            .uri("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.accountId").isEqualTo("ACC123");
        
        // GREEN: Implement controller endpoint
        // REFACTOR: Add validation, error handling
    }
    
    @Test
    void shouldReturn400WhenInvalidRequest() {
        // Test validation
        CreateAccountRequest invalid = new CreateAccountRequest("", -10.0);
        
        webTestClient.post()
            .uri("/api/v1/accounts")
            .bodyValue(invalid)
            .exchange()
            .expectStatus().isBadRequest();
    }
}
```

## Testing Best Practices

### Test Structure (AAA Pattern)
```java
@Test
void testName() {
    // ARRANGE: Set up test data and mocks
    
    // ACT: Execute the behavior under test
    
    // ASSERT: Verify outcomes
}
```

### Test Naming Convention
Use descriptive names that express intent:
- `shouldReturnAccountWhenIdExists()`
- `shouldThrowInsufficientFundsWhenBalanceTooLow()`
- `shouldPublishEventWhenTransferCompleted()`

### StepVerifier Usage
Always use `StepVerifier` for reactive code:
```java
StepVerifier.create(monoOrFlux)
    .expectNext(expectedValue)
    .expectError(ExpectedException.class)
    .verifyComplete();
```

Never use `.block()` in tests unless testing error cases that must not happen.

### Test Coverage Requirements
- **Domain Layer**: 100% line and branch coverage
- **Application Layer**: 95%+ coverage
- **Infrastructure**: 85%+ for repositories, 100% for custom queries
- **API Layer**: 90%+ coverage
- **Integration Tests**: Critical paths and edge cases

### Test Independence
- Each test must be runnable in isolation
- No shared mutable state between tests
- Use `@BeforeEach` for test setup, not `@BeforeAll`
- Clean up Testcontainers resources properly

## Code Quality Standards

### Immutability
- Prefer immutable objects (final fields, no setters)
- Use Lombok `@Value` or Java records for DTOs
- Domain aggregates may have controlled mutability via methods

### Error Handling
- Never swallow exceptions
- Create domain-specific exceptions extending `RuntimeException`
- Map exceptions to appropriate HTTP status codes
- Include correlation IDs in error responses

### Validation
- Validate at API boundary (controllers)
- Enforce invariants in domain constructors
- Use Bean Validation where appropriate
- Fail fast with clear error messages

### Reactive Error Patterns
```java
// Correct: reactive error handling
return repository.findById(id)
    .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
    .flatMap(account -> account.debit(amount))
    .onErrorMap(DomainException.class, e -> new BusinessRuleViolationException(e));

// Wrong: blocking error handling
try {
    Account account = repository.findById(id).block(); // DON'T DO THIS
} catch (Exception e) {
    // ...
}
```

## Implementation Order for New Features

### 1. Define Domain Model (TDD)
- Write value object tests
- Write aggregate tests with business rules
- Write domain event definitions
- Implement to make tests pass

### 2. Create Application Layer (TDD)
- Write command handler tests with mocks
- Write query handler tests with mocks
- Implement handlers
- Add validation logic

### 3. Implement Infrastructure (TDD)
- Write repository integration tests
- Write messaging producer/consumer tests
- Implement infrastructure code
- Verify with Testcontainers

### 4. Add API Layer (TDD)
- Write controller slice tests
- Implement endpoints
- Add OpenAPI documentation
- Test with WebTestClient

### 5. End-to-End Testing
- Write full integration tests with all components
- Use Testcontainers for PostgreSQL and RabbitMQ
- Verify complete flows
- Test failure scenarios

## Example: Adding a New Feature (Transfer Money)

### Step 1: Domain Tests
```java
@Test
void shouldCreateTransferWithPendingStatus() {
    Transfer transfer = Transfer.initiate("TX123", "ACC1", "ACC2", Money.of(50));
    
    assertThat(transfer.getStatus()).isEqualTo(TransferStatus.PENDING);
    assertThat(transfer.getDomainEvents())
        .hasSize(1)
        .first()
        .isInstanceOf(TransferInitiated.class);
}
```

### Step 2: Command Handler Tests
```java
@Test
void shouldDebitSourceAndCreditTargetAccount() {
    InitiateTransferCommand command = new InitiateTransferCommand("ACC1", "ACC2", Money.of(50));
    
    when(accountRepo.findById("ACC1")).thenReturn(Mono.just(sourceAccount));
    when(accountRepo.findById("ACC2")).thenReturn(Mono.just(targetAccount));
    when(accountRepo.saveAll(anyList())).thenReturn(Flux.just(sourceAccount, targetAccount));
    when(eventPublisher.publish(any())).thenReturn(Mono.empty());
    
    Mono<TransferResult> result = handler.handle(command);
    
    StepVerifier.create(result)
        .assertNext(r -> assertThat(r.transferId()).isNotEmpty())
        .verifyComplete();
}
```

### Step 3: Repository Tests
```java
@Test
void shouldSaveTransferAtomically() {
    Transfer transfer = Transfer.initiate("TX123", "ACC1", "ACC2", Money.of(50));
    
    Mono<Transfer> saved = transferRepository.save(transfer);
    
    StepVerifier.create(saved)
        .assertNext(t -> assertThat(t.getId()).isEqualTo("TX123"))
        .verifyComplete();
}
```

### Step 4: Controller Tests
```java
@Test
void shouldReturn202WhenTransferInitiated() {
    TransferRequest request = new TransferRequest("ACC1", "ACC2", 50.0);
    
    when(handler.handle(any())).thenReturn(Mono.just(new TransferResult("TX123")));
    
    webTestClient.post()
        .uri("/api/v1/transfers")
        .bodyValue(request)
        .exchange()
        .expectStatus().isAccepted()
        .expectBody()
        .jsonPath("$.transferId").isEqualTo("TX123");
}
```

### Step 5: Integration Test
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class TransferIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");
    
    @Autowired
    private WebTestClient client;
    
    @Test
    void shouldCompleteTransferEndToEnd() {
        // Create source account
        // Create target account
        // Initiate transfer
        // Verify both accounts updated
        // Verify events published
    }
}
```

## Common Pitfalls to Avoid

1. **Writing production code before tests** → Always write test first
2. **Testing implementation details** → Test behavior, not internals
3. **Mocking too much** → Use real objects when simple
4. **Not testing error paths** → Test failures as thoroughly as success
5. **Using `.block()` in production** → Keep everything reactive
6. **Ignoring test performance** → Keep unit tests fast (<100ms)
7. **Not cleaning test data** → Use transactions or container recreation
8. **Tight coupling in tests** → Mock dependencies at boundaries
9. **Not verifying mock interactions** → Use `verify()` when side effects matter
10. **Premature optimization** → Make it work, make it right, make it fast

## Refactoring Guidelines

After getting tests green:
- Extract magic numbers to constants
- Extract complex conditions to methods with meaningful names
- Remove duplication (DRY principle)
- Improve naming for clarity
- Add documentation for non-obvious logic
- Verify all tests still pass after each refactoring step

## Documentation Requirements

For each public API:
- JavaDoc with description, parameters, return values, exceptions
- Include examples for complex scenarios
- Document thread-safety and reactive behavior
- Reference related domain concepts

## Summary Checklist for Every Change

- [ ] Test written first (RED)
- [ ] Minimal code to pass test (GREEN)
- [ ] Code refactored for quality (REFACTOR)
- [ ] No `.block()` in production code
- [ ] Reactive types propagated throughout
- [ ] Error handling implemented reactively
- [ ] Domain events published for state changes
- [ ] CQRS separation maintained
- [ ] Integration test added for new flows
- [ ] Documentation updated
- [ ] All tests pass
- [ ] Coverage requirements met

---

**Remember**: TDD is not about writing tests; it's about designing better code through tests. Let tests drive your design decisions.
```

