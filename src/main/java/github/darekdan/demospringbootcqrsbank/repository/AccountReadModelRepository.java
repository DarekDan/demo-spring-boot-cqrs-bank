package github.darekdan.demospringbootcqrsbank.repository;

import github.darekdan.demospringbootcqrsbank.domain.AccountReadModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountReadModelRepository extends ReactiveCrudRepository<AccountReadModel, String> {
    Mono<AccountReadModel> findByAccountId(String accountId);
}
