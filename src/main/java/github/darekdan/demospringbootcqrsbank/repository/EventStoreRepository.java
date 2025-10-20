package github.darekdan.demospringbootcqrsbank.repository;

import github.darekdan.demospringbootcqrsbank.domain.StoredEvent;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStoreRepository extends ReactiveCrudRepository<StoredEvent, Long> {
    Flux<StoredEvent> findByAccountIdOrderByOccurredAtAsc(String accountId);

    Mono<StoredEvent> findByAccountIdAndEventType(String accountId, String eventType);
}
