package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import poc.genericresourcemanagement.GenericResourceManagementApplication;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {GenericResourceManagementApplication.class}
)
@ActiveProfiles("cucumber")
@CucumberContextConfiguration
@SuppressWarnings("unused")
public class GenericResourceManagementCucumberTestConfig {
}
