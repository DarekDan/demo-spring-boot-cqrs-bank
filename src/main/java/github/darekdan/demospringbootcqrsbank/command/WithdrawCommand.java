package github.darekdan.demospringbootcqrsbank.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawCommand {
    @JsonProperty("account_id")
    private String accountId;
    private BigDecimal amount;
}
