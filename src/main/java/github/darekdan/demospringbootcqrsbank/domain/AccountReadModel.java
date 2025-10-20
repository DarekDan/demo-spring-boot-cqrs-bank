package github.darekdan.demospringbootcqrsbank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("account_read_model")
public class AccountReadModel {
    @Id
    private String accountId;
    private String accountHolder;
    private BigDecimal balance;
    private String status;
    private Long transactionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
