package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.java8.LambdaGlue;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.web.server.LocalServerPort;
import poc.genericresourcemanagement.test.cucumber.service.ManualTimeGenerator;

import static org.apache.logging.log4j.util.Unbox.box;

@Log4j2
@SuppressWarnings("unused")
public class GenericResourceManagementCucumberTestHook implements LambdaGlue {
    public GenericResourceManagementCucumberTestHook(
            @LocalServerPort final int port,
            final ManualTimeGenerator manualTimeGenerator
    ) {
        Before(0, scenario -> {
            RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
            log.info("going to set the RestAssured.port to: {}", box(port));
            RestAssured.port = port;

            final String time = "2023-01-01T00:00:00.000";
            manualTimeGenerator.setCurrentLocalDateTime(time);
            log.info("going to set the current time to: {}", time);
        });

        After(0, scenario -> RestAssured.reset());
    }
}
