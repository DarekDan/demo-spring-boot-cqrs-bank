package github.darekdan.demospringbootcqrsbank;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("bankdb")
                .withUsername("postgres")
                .withPassword("postgres");
    }

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:latest")).withExposedPorts(5672);
    }

    @Bean
    @Primary
    ConnectionFactory connectionFactory(PostgreSQLContainer<?> postgresContainer) {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "pool")
                .option(ConnectionFactoryOptions.PROTOCOL, "postgresql")
                .option(ConnectionFactoryOptions.HOST, postgresContainer.getHost())
                .option(ConnectionFactoryOptions.PORT, postgresContainer.getFirstMappedPort())
                .option(ConnectionFactoryOptions.DATABASE, postgresContainer.getDatabaseName())
                .option(ConnectionFactoryOptions.USER, postgresContainer.getUsername())
                .option(ConnectionFactoryOptions.PASSWORD, postgresContainer.getPassword())
                .option(ConnectionFactoryOptions.SSL, false)
                .build();

        return ConnectionFactoryBuilder.withOptions(ConnectionFactoryOptions.builder().from(options)).build();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        // RabbitMQ properties will be auto-configured from the bean
        registry.add("spring.rabbitmq.listener.simple.acknowledge-mode", () -> "manual");
        registry.add("spring.rabbitmq.listener.simple.retry.enabled", () -> "true");
        registry.add("spring.rabbitmq.listener.simple.retry.max-attempts", () -> "3");
        registry.add("spring.rabbitmq.listener.simple.retry.initial-interval", () -> "1000");
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));

        initializer.setDatabasePopulator(populator);
        return initializer;
    }

}
