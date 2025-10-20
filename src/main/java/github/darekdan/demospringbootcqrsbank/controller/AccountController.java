package github.darekdan.demospringbootcqrsbank.controller;

import github.darekdan.demospringbootcqrsbank.command.CommandHandler;
import github.darekdan.demospringbootcqrsbank.command.CreateAccountCommand;
import github.darekdan.demospringbootcqrsbank.command.DepositCommand;
import github.darekdan.demospringbootcqrsbank.command.WithdrawCommand;
import github.darekdan.demospringbootcqrsbank.domain.Account;
import github.darekdan.demospringbootcqrsbank.event.DepositedEvent;
import github.darekdan.demospringbootcqrsbank.event.InsufficientFundsEvent;
import github.darekdan.demospringbootcqrsbank.event.WithdrawnEvent;
import github.darekdan.demospringbootcqrsbank.query.QueryHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final CommandHandler commandHandler;
    private final QueryHandler queryHandler;

    public AccountController(CommandHandler commandHandler, QueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createAccount(@RequestBody CreateAccountCommand cmd) {
        return commandHandler.handleCreateAccount(cmd)
                .map(event -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Account created successfully");
                    response.put("accountId", event.getAccountId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("status", "error", "message", e.getMessage()))
                ));
    }

    @GetMapping("/{accountId}")
    public Mono<ResponseEntity<Account>> getAccount(@PathVariable String accountId) {
        return queryHandler.getAccountDetails(accountId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.notFound().build()
                ));
    }

    @PostMapping("/{accountId}/deposit")
    public Mono<ResponseEntity<Map<String, Object>>> deposit(
            @PathVariable String accountId,
            @RequestBody Map<String, Object> request) {

        DepositCommand cmd = new DepositCommand(
                accountId,
                new java.math.BigDecimal(request.get("amount").toString())
        );

        return commandHandler.handleDeposit(cmd)
                .map(event -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Deposit successful");
                    response.put("newBalance", ((DepositedEvent) event).getNewBalance());
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("status", "error", "message", e.getMessage()))
                ));
    }

    @PostMapping("/{accountId}/withdraw")
    public Mono<ResponseEntity<Map<String, Object>>> withdraw(
            @PathVariable String accountId,
            @RequestBody Map<String, Object> request) {

        WithdrawCommand cmd = new WithdrawCommand(
                accountId,
                new java.math.BigDecimal(request.get("amount").toString())
        );

        return commandHandler.handleWithdraw(cmd)
                .map(event -> {
                    Map<String, Object> response = new HashMap<>();
                    if (event instanceof WithdrawnEvent) {
                        response.put("status", "success");
                        response.put("message", "Withdrawal successful");
                        response.put("newBalance", ((WithdrawnEvent) event).getNewBalance());
                    } else if (event instanceof InsufficientFundsEvent) {
                        response.put("status", "failed");
                        response.put("message", "Insufficient funds");
                        response.put("currentBalance", ((InsufficientFundsEvent) event).getCurrentBalance());
                    }
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("status", "error", "message", e.getMessage()))
                ));
    }
}
