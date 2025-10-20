package github.darekdan.demospringbootcqrsbank.command;

import github.darekdan.demospringbootcqrsbank.domain.Account;
import github.darekdan.demospringbootcqrsbank.event.*;
import github.darekdan.demospringbootcqrsbank.repository.AccountRepository;
import github.darekdan.demospringbootcqrsbank.repository.EventStoreRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CommandHandler {
    private final AccountRepository accountRepo;
    private final EventStoreRepository eventStoreRepo;
    private final RabbitTemplate rabbitTemplate;

    public CommandHandler(AccountRepository accountRepo,
                          EventStoreRepository eventStoreRepo,
                          RabbitTemplate rabbitTemplate) {
        this.accountRepo = accountRepo;
        this.eventStoreRepo = eventStoreRepo;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<AccountCreatedEvent> handleCreateAccount(CreateAccountCommand cmd) {
        return accountRepo.findByAccountId(cmd.getAccountId())
                .flatMap(existing -> Mono.error(new RuntimeException("Account already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    Account account = new Account();
                    account.setAccountId(cmd.getAccountId());
                    account.setAccountHolder(cmd.getAccountHolder());
                    account.setBalance(cmd.getInitialBalance());
                    account.setStatus("ACTIVE");
                    account.setCreatedAt(LocalDateTime.now());
                    account.setUpdatedAt(LocalDateTime.now());

                    return accountRepo.save(account)
                            .flatMap(saved -> {
                                AccountCreatedEvent event = new AccountCreatedEvent(
                                        UUID.randomUUID().toString(),
                                        cmd.getAccountId(),
                                        cmd.getAccountHolder(),
                                        cmd.getInitialBalance(),
                                        LocalDateTime.now()
                                );
                                return storeEvent("AccountCreatedEvent", cmd.getAccountId(), event)
                                        .then(publishEvent("account.created", event))
                                        .then(Mono.just(event));
                            });
                })).map(o->(AccountCreatedEvent)o);
    }

    public Mono<DepositedEvent> handleDeposit(DepositCommand cmd) {
        return accountRepo.findByAccountId(cmd.getAccountId())
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
                .flatMap(account -> {
                    if ("INACTIVE".equals(account.getStatus())) {
                        return Mono.error(new RuntimeException("Account is inactive"));
                    }
                    BigDecimal newBalance = account.getBalance().add(cmd.getAmount());
                    account.setBalance(newBalance);
                    account.setUpdatedAt(LocalDateTime.now());

                    return accountRepo.save(account)
                            .flatMap(updated -> {
                                DepositedEvent event = new DepositedEvent(
                                        UUID.randomUUID().toString(),
                                        cmd.getAccountId(),
                                        cmd.getAmount(),
                                        newBalance,
                                        LocalDateTime.now()
                                );
                                return storeEvent("DepositedEvent", cmd.getAccountId(), event)
                                        .then(publishEvent("account.deposited", event))
                                        .then(Mono.just(event));
                            });
                });
    }

    public Mono<Object> handleWithdraw(WithdrawCommand cmd) {
        return accountRepo.findByAccountId(cmd.getAccountId())
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
                .flatMap(account -> {
                    if ("INACTIVE".equals(account.getStatus())) {
                        return Mono.error(new RuntimeException("Account is inactive"));
                    }
                    if (account.getBalance().compareTo(cmd.getAmount()) < 0) {
                        InsufficientFundsEvent event = new InsufficientFundsEvent(
                                UUID.randomUUID().toString(),
                                cmd.getAccountId(),
                                cmd.getAmount(),
                                account.getBalance(),
                                LocalDateTime.now()
                        );
                        return storeEvent("InsufficientFundsEvent", cmd.getAccountId(), event)
                                .then(publishEvent("account.insufficient.funds", event))
                                .then(Mono.just(event));
                    }

                    BigDecimal newBalance = account.getBalance().subtract(cmd.getAmount());
                    account.setBalance(newBalance);
                    account.setUpdatedAt(LocalDateTime.now());

                    return accountRepo.save(account)
                            .flatMap(updated -> {
                                WithdrawnEvent event = new WithdrawnEvent(
                                        UUID.randomUUID().toString(),
                                        cmd.getAccountId(),
                                        cmd.getAmount(),
                                        newBalance,
                                        LocalDateTime.now()
                                );
                                return storeEvent("WithdrawnEvent", cmd.getAccountId(), event)
                                        .then(publishEvent("account.withdrawn", event))
                                        .then(Mono.just(event));
                            });
                });
    }

    private Mono<Void> storeEvent(String eventType, String accountId, Object event) {
        StoredEvent stored = new StoredEvent();
        stored.setAccountId(accountId);
        stored.setEventType(eventType);
        stored.setEventData(event.toString());
        stored.setOccurredAt(LocalDateTime.now());
        stored.setStoredAt(LocalDateTime.now());

        return eventStoreRepo.save(stored).then();
    }

    private Mono<Void> publishEvent(String routingKey, Object event) {
        return Mono.fromRunnable(() ->
                rabbitTemplate.convertAndSend("bank-events", routingKey, event)
        );
    }
}
