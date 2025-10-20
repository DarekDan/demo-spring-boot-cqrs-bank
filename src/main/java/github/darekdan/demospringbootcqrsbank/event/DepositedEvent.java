package github.darekdan.demospringbootcqrsbank.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositedEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("account_id")
    private String accountId;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private LocalDateTime timestamp;
}
