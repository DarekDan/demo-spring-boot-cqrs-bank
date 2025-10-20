package github.darekdan.demospringbootcqrsbank;

import github.darekdan.demospringbootcqrsbank.command.CommandHandler;
import github.darekdan.demospringbootcqrsbank.command.CreateAccountCommand;
import github.darekdan.demospringbootcqrsbank.command.DepositCommand;
import github.darekdan.demospringbootcqrsbank.constants.EventTypes;
import github.darekdan.demospringbootcqrsbank.constants.MessageKeys;
import github.darekdan.demospringbootcqrsbank.event.DepositedEvent;
import github.darekdan.demospringbootcqrsbank.repository.AccountRepository;
import github.darekdan.demospringbootcqrsbank.repository.EventStoreRepository;
import github.darekdan.demospringbootcqrsbank.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@DisplayName("Account Command Handler Tests")
class AccountCommandHandlerTests {



    @Autowired
    private CommandHandler commandHandler;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EventStoreRepository eventStoreRepository;

    @Autowired
    private MessageService messageService;

    @Test
    @DisplayName("Should create account successfully")
    void testCreateAccountSuccess() {
        CreateAccountCommand cmd = new CreateAccountCommand(
                "ACC-001",
                "John Doe",
                new BigDecimal("1000.00")
        );

        StepVerifier.create(commandHandler.handleCreateAccount(cmd))
                .assertNext(event -> {
                    assertNotNull(event);
                    assertEquals("ACC-001", event.getAccountId());
                    assertEquals("John Doe", event.getAccountHolder());
                    assertEquals(new BigDecimal("1000.00"), event.getInitialBalance());
                    assertNotNull(event.getEventId());
                    assertNotNull(event.getTimestamp());
                })
                .expectComplete()
                .verify(Duration.ofSeconds(100));

        StepVerifier.create(accountRepository.findByAccountId("ACC-001"))
                .assertNext(account -> {
                    assertEquals("ACC-001", account.getAccountId());
                    assertEquals("ACTIVE", account.getStatus());
                    assertEquals(new BigDecimal("1000.00"), account.getBalance());
                })
                .expectComplete()
                .verify(Duration.ofSeconds(10));

        StepVerifier
                .create(eventStoreRepository.findByAccountIdAndEventType("ACC-001", EventTypes.ACCOUNT_CREATED))
                .assertNext(storedEvent -> {
                    assertEquals("ACC-001", storedEvent.getAccountId());
                    assertEquals(EventTypes.ACCOUNT_CREATED, storedEvent.getEventType());
                })
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Should fail creating duplicate account")
    void testCreateAccountDuplicate() {
        String accountId = "ACC-DUP-" + System.currentTimeMillis();
        CreateAccountCommand cmd = new CreateAccountCommand(
                accountId,
                "Jane Doe",
                new BigDecimal("500.00")
        );

        StepVerifier.create(commandHandler.handleCreateAccount(cmd)
                        .then(commandHandler.handleCreateAccount(cmd)))
                .expectErrorMessage(messageService.getMessage(MessageKeys.ERROR_ACCOUNT_ALREADY_EXISTS, accountId))
                .verify(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Should deposit to account successfully")
    void testDepositSuccess() {
        CreateAccountCommand createCmd = new CreateAccountCommand(
                "ACC-DEP-001",
                "Bob Smith",
                new BigDecimal("1000.00")
        );

        DepositCommand depositCmd = new DepositCommand(
                "ACC-DEP-001",
                new BigDecimal("500.00")
        );

        StepVerifier.create(commandHandler.handleCreateAccount(createCmd)
                        .then(commandHandler.handleDeposit(depositCmd)))
                .assertNext(event -> {
                    assertNotNull(event);
                    DepositedEvent depositedEvent = (DepositedEvent) event;
                    assertEquals("ACC-DEP-001", depositedEvent.getAccountId());
                    assertEquals(new BigDecimal("500.00"), depositedEvent.getAmount());
                    assertEquals(new BigDecimal("1500.00"), depositedEvent.getNewBalance());
                    assertNotNull(depositedEvent.getEventId());
                    assertNotNull(depositedEvent.getTimestamp());
                })
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Should fail depositing to non-existent account")
    void testDepositNonExistentAccount() {
        DepositCommand cmd = new DepositCommand(
                "ACC-INVALID-" + System.currentTimeMillis(),
                new BigDecimal("100.00")
        );

        StepVerifier.create(commandHandler.handleDeposit(cmd))
                .expectErrorMessage("Account not found")
                .verify(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Should fail depositing negative amount")
    void testDepositNegativeAmount() {
        CreateAccountCommand createCmd = new CreateAccountCommand(
                "ACC-NEG-001",
                "Negative Test",
                new BigDecimal("1000.00")
        );

        DepositCommand depositCmd = new DepositCommand(
                "ACC-NEG-001",
                new BigDecimal("-100.00")
        );

        StepVerifier
                .create(commandHandler.handleCreateAccount(createCmd)
                        .then(commandHandler.handleDeposit(depositCmd)))
                .expectErrorMessage("Deposit amount must be positive")
                .verify(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Should deposit multiple times to same account")
    void testMultipleDeposits() {
        String accountId = "ACC-MULTI-DEP-" + System.currentTimeMillis();
        CreateAccountCommand createCmd = new CreateAccountCommand(
                accountId,
                "Multi Deposit User",
                new BigDecimal("1000.00")
        );

        DepositCommand deposit1 = new DepositCommand(accountId, new BigDecimal("100.00"));
        DepositCommand deposit2 = new DepositCommand(accountId, new BigDecimal("200.00"));

        StepVerifier.create(
                        commandHandler.handleCreateAccount(createCmd)
                                .then(commandHandler.handleDeposit(deposit1))
                                .then(commandHandler.handleDeposit(deposit2))
                )
                .assertNext(event -> {
                    DepositedEvent depositedEvent = (DepositedEvent) event;
                    assertEquals(new BigDecimal("1300.00"), depositedEvent.getNewBalance());
                })
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }
}
