package github.darekdan.demospringbootcqrsbank.constants;

import java.math.BigDecimal;

/**
 * Domain-specific constants for business rules.
 */
public final class DomainConstants {
    
    private DomainConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Account Status
    public static final String ACCOUNT_STATUS_ACTIVE = "ACTIVE";
    public static final String ACCOUNT_STATUS_SUSPENDED = "SUSPENDED";
    public static final String ACCOUNT_STATUS_CLOSED = "CLOSED";
    
    // Business Rules
    public static final BigDecimal MINIMUM_INITIAL_BALANCE = BigDecimal.ZERO;
    public static final BigDecimal MINIMUM_DEPOSIT_AMOUNT = new BigDecimal("0.01");
    public static final BigDecimal MINIMUM_WITHDRAWAL_AMOUNT = new BigDecimal("0.01");
    public static final BigDecimal MAXIMUM_TRANSACTION_AMOUNT = new BigDecimal("1000000.00");
    
    // Account Limits
    public static final int MAX_ACCOUNT_HOLDER_NAME_LENGTH = 255;
    public static final int MIN_ACCOUNT_HOLDER_NAME_LENGTH = 2;
}
