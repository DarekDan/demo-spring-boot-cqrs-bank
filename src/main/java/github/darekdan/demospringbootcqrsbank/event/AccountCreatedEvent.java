package github.darekdan.demospringbootcqrsbank.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent {
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("account_holder")
    private String accountHolder;
    private BigDecimal initialBalance;
    private LocalDateTime timestamp;
}


