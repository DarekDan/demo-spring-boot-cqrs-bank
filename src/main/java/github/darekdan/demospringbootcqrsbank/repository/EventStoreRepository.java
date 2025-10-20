package github.darekdan.demospringbootcqrsbank.repository;

import github.darekdan.demospringbootcqrsbank.domain.StoredEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EventStoreRepository extends ReactiveCrudRepository<StoredEvent, Long> {
    Flux<StoredEvent> findByAccountIdOrderByOccurredAtAsc(String accountId);
}
