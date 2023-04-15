package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import poc.genericresourcemanagement.GenericResourceManagementApplication;
import poc.genericresourcemanagement.application.service.TimeGenerator;
import poc.genericresourcemanagement.test.cucumber.service.ManualTimeGenerator;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                GenericResourceManagementCucumberTestConfig.Config.class,
                GenericResourceManagementApplication.class
        }
)
@ActiveProfiles("cucumber")
@CucumberContextConfiguration
@Log4j2
@SuppressWarnings("unused")
public class GenericResourceManagementCucumberTestConfig {
    public static class Config {
        @Bean
        TimeGenerator ManualTimeGenerator() {
            log.info("Using DefaultTimeGenerator");
            return new ManualTimeGenerator();
        };
    }
}
