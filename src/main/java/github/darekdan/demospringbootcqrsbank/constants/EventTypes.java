package github.darekdan.demospringbootcqrsbank.constants;

/**
 * Event type identifiers for event sourcing.
 */
public final class EventTypes {
    
    private EventTypes() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static final String ACCOUNT_CREATED = "AccountCreatedEvent";
    public static final String DEPOSITED = "DepositedEvent";
    public static final String WITHDRAWN = "WithdrawnEvent";
    public static final String INSUFFICIENT_FUNDS = "InsufficientFundsEvent";
}
