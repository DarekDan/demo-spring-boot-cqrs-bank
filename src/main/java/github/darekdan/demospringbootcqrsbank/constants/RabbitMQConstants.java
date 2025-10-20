package github.darekdan.demospringbootcqrsbank.constants;

/**
 * RabbitMQ queue and exchange constants.
 */
public final class RabbitMQConstants {
    
    private RabbitMQConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Exchange Names
    public static final String EVENT_EXCHANGE = "bank.events.exchange";
    
    // Queue Names
    public static final String ACCOUNT_EVENTS_QUEUE = "account.events.queue";
    public static final String PROJECTION_QUEUE = "projection.queue";
    
    // Routing Keys
    public static final String ACCOUNT_CREATED_ROUTING_KEY = "account.created";
    public static final String ACCOUNT_DEPOSITED_ROUTING_KEY = "account.deposited";
    public static final String ACCOUNT_WITHDRAWN_ROUTING_KEY = "account.withdrawn";
    public static final String INSUFFICIENT_FUNDS_ROUTING_KEY = "account.insufficient.funds";
}
