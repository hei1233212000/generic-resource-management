package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.java8.En;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.Matchers;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SuppressWarnings("unused")
public class ManageResourceSteps implements En {
    public ManageResourceSteps(
            final ResourceRepository resourceRepository
    ) {
        Given("there is no resource exist", () -> assertThat(resourceRepository.count().block())
                .isZero());

        When("query all {resourceType} resource", (ResourceDomainModel.ResourceType resourceType) -> when()
                .get("/resources/" + resourceType)
                .then()
                .statusCode(200)
                .assertThat()
                .body("$", Matchers.hasSize(0)));

        ParameterType("resourceType", ".*",
                (String resourceType) -> ResourceDomainModel.ResourceType.valueOf(resourceType));
    }
}
