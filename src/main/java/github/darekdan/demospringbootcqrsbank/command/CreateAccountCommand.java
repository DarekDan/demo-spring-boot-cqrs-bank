package github.darekdan.demospringbootcqrsbank.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreateAccountCommand {
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("account_holder")
    private String accountHolder;
    private BigDecimal initialBalance;
}

