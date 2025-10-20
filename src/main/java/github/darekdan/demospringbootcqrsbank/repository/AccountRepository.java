package github.darekdan.demospringbootcqrsbank.repository;

import github.darekdan.demospringbootcqrsbank.domain.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
    Mono<Account> findByAccountId(String accountId);
}
