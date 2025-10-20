package github.darekdan.demospringbootcqrsbank.constants;

/**
 * Message keys for internationalization.
 * These keys reference messages in messages.properties files.
 */
public final class MessageKeys {
    
    private MessageKeys() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Validation Messages
    public static final String VALIDATION_ACCOUNT_HOLDER_REQUIRED = "validation.account.holder.required";
    public static final String VALIDATION_INITIAL_BALANCE_NON_NEGATIVE = "validation.initial.balance.non.negative";
    public static final String VALIDATION_DEPOSIT_AMOUNT_POSITIVE = "validation.deposit.amount.positive";
    public static final String VALIDATION_WITHDRAW_AMOUNT_POSITIVE = "validation.withdraw.amount.positive";
    public static final String VALIDATION_ACCOUNT_ID_REQUIRED = "validation.account.id.required";
    
    // Business Logic Messages
    public static final String ERROR_INSUFFICIENT_FUNDS = "error.insufficient.funds";
    public static final String ERROR_ACCOUNT_NOT_FOUND = "error.account.not.found";
    public static final String ERROR_ACCOUNT_ALREADY_EXISTS = "error.account.already.exists";
    public static final String ERROR_INVALID_ACCOUNT_STATUS = "error.invalid.account.status";
    
    // Success Messages
    public static final String SUCCESS_ACCOUNT_CREATED = "success.account.created";
    public static final String SUCCESS_DEPOSIT_COMPLETED = "success.deposit.completed";
    public static final String SUCCESS_WITHDRAWAL_COMPLETED = "success.withdrawal.completed";
    
    // Event Messages
    public static final String EVENT_ACCOUNT_CREATED = "event.account.created";
    public static final String EVENT_DEPOSITED = "event.deposited";
    public static final String EVENT_WITHDRAWN = "event.withdrawn";
    public static final String EVENT_INSUFFICIENT_FUNDS = "event.insufficient.funds";
    
    // General Messages
    public static final String INFO_PROCESSING_COMMAND = "info.processing.command";
    public static final String INFO_QUERY_EXECUTED = "info.query.executed";
}
