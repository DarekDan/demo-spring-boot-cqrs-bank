package github.darekdan.demospringbootcqrsbank.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public TopicExchange bankEventsExchange() {
        return new TopicExchange("bank-events", true, false);
    }

    @Bean
    public Queue accountCreatedQueue() {
        return new Queue("account.created.queue", true);
    }

    @Bean
    public Queue accountDepositedQueue() {
        return new Queue("account.deposited.queue", true);
    }

    @Bean
    public Queue accountWithdrawnQueue() {
        return new Queue("account.withdrawn.queue", true);
    }

    @Bean
    public Queue accountInsufficientFundsQueue() {
        return new Queue("account.insufficient.funds.queue", true);
    }

    @Bean
    public Binding accountCreatedBinding(Queue accountCreatedQueue, TopicExchange bankEventsExchange) {
        return BindingBuilder.bind(accountCreatedQueue)
                .to(bankEventsExchange)
                .with("account.created");
    }

    @Bean
    public Binding accountDepositedBinding(Queue accountDepositedQueue, TopicExchange bankEventsExchange) {
        return BindingBuilder.bind(accountDepositedQueue)
                .to(bankEventsExchange)
                .with("account.deposited");
    }

    @Bean
    public Binding accountWithdrawnBinding(Queue accountWithdrawnQueue, TopicExchange bankEventsExchange) {
        return BindingBuilder.bind(accountWithdrawnQueue)
                .to(bankEventsExchange)
                .with("account.withdrawn");
    }

    @Bean
    public Binding accountInsufficientFundsBinding(Queue accountInsufficientFundsQueue, TopicExchange bankEventsExchange) {
        return BindingBuilder.bind(accountInsufficientFundsQueue)
                .to(bankEventsExchange)
                .with("account.insufficient.funds");
    }
}
