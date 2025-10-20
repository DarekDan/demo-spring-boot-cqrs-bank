package github.darekdan.demospringbootcqrsbank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("event_store")
public class StoredEvent {
    @Id
    private Long id;
    private String accountId;
    private String eventType;
    private String eventData;
    private LocalDateTime occurredAt;
    private LocalDateTime storedAt;
}
