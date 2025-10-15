package github.darekdan.demospringbootcqrsbank;

import org.springframework.boot.SpringApplication;

public class TestDemoSpringBootCqrsBankApplication {

    public static void main(String[] args) {
        SpringApplication.from(DemoSpringBootCqrsBankApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
