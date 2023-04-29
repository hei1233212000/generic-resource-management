package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.java8.LambdaGlue;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import poc.genericresourcemanagement.test.cucumber.service.ManualTimeGenerator;

import java.util.List;

import static org.apache.logging.log4j.util.Unbox.box;

@Log4j2
@SuppressWarnings("unused")
public class GenericResourceManagementCucumberTestHook implements LambdaGlue {
    public GenericResourceManagementCucumberTestHook(
            @LocalServerPort final int port,
            final ManualTimeGenerator manualTimeGenerator,
            final R2dbcEntityTemplate r2dbcEntityTemplate,
            final List<R2dbcRepository<?, ?>> r2dbcRepositories
    ) {
        Before(0, scenario -> {
            RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
            log.info("going to set the RestAssured.port to: {}", box(port));
            RestAssured.port = port;

            final String time = "2023-01-01T00:00:00.000";
            manualTimeGenerator.setCurrentLocalDateTime(time);
            log.info("going to set the current time to: {}", time);

            cleanUpDatabase(r2dbcRepositories);
            resetDbSequencers(r2dbcEntityTemplate);
        });

        After(0, scenario -> RestAssured.reset());
    }

    private static void cleanUpDatabase(final List<R2dbcRepository<?, ?>> r2dbcRepositories) {
        r2dbcRepositories.forEach(r2dbcRepository -> {
            r2dbcRepository.deleteAll().block();
            log.info("all data are deleted by {}", r2dbcRepository);
        });
    }

    private static void resetDbSequencers(final R2dbcEntityTemplate r2dbcEntityTemplate) {
        resetDbSequence(r2dbcEntityTemplate, "USER_REQUEST_ID_SEQ");
        resetDbSequence(r2dbcEntityTemplate, "USER_ID_SEQ");
    }

    private static void resetDbSequence(
            final R2dbcEntityTemplate r2dbcEntityTemplate,
            final String sequenceName
    ) {
        r2dbcEntityTemplate.getDatabaseClient()
                .sql("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1")
                .then()
                .block();
        log.info("{} is reset", sequenceName);
    }
}
