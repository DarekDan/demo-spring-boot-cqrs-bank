package github.darekdan.demospringbootcqrsbank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DemoSpringBootCqrsBankApplicationTests {

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
    }

}
