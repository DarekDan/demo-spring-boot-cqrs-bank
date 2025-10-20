package github.darekdan.demospringbootcqrsbank.query;

import github.darekdan.demospringbootcqrsbank.domain.AccountReadModel;
import github.darekdan.demospringbootcqrsbank.repository.AccountReadModelRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class QueryHandler {
    private final AccountReadModelRepository readModelRepo;

    public QueryHandler(AccountReadModelRepository readModelRepo) {
        this.readModelRepo = readModelRepo;
    }

    public Mono<AccountReadModel> getAccountDetails(String accountId) {
        return readModelRepo.findByAccountId(accountId)
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")));
    }
}
