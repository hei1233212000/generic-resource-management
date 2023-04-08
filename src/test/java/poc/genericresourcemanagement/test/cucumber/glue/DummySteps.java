package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.java8.En;
import lombok.extern.log4j.Log4j2;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class DummySteps implements En {
    public DummySteps() {
        When("Say hello", () -> {
            log.info("Hello");
            assertThat(1 * 1 == 1).isTrue();
        });
    }
}
