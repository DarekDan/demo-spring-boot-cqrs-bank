package github.darekdan.demospringbootcqrsbank.query;

import github.darekdan.demospringbootcqrsbank.domain.Account;
import github.darekdan.demospringbootcqrsbank.repository.AccountRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class QueryHandler {
    private final AccountRepository accountRepo;

    public QueryHandler(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    public Mono<Account> getAccountDetails(String accountId) {
        return accountRepo.findByAccountId(accountId)
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")));
    }
}
