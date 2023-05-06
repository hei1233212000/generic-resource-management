package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.test.cucumber.common.Verifications;

import java.util.List;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SuppressWarnings("unused")
public class ResourceSteps implements En {
    private Response response;

    public ResourceSteps(
            final ObjectMapper objectMapper
    ) {
        When("I query {resourceType} resource by id {string}",
                (ResourceType resourceType, String resourceId) -> {
                    response = when().get("/resources/{resourceType}/{resourceId}", resourceType, resourceId);
                });

        Then("query {resourceType} resource by id {string} should return the base info:",
                (ResourceType resourceType, String resourceId, DataTable dataTable) -> {
                    response = when().get("/resources/{resourceType}/{resourceId}", resourceType, resourceId);
                    final JsonNode actualResult = objectMapper.readTree(response.body().asString());
                    Verifications.verifyJsonNode(dataTable, actualResult);
                });
        Then("the query resource is failed with http status code {int} with error messages:",
                (Integer expectedHttpStatusCode, DataTable dataTable) -> {
                    response.then().statusCode(expectedHttpStatusCode);

                    final List<String> expectedErrorMessages = dataTable.asList();
                    final JsonNode responseNode = objectMapper.readTree(response.body().asString());
                    final List<String> actualErrorMessages = objectMapper.convertValue(
                            responseNode.get("errorMessages"),
                            new TypeReference<>() {}
                    );
                    assertThat(actualErrorMessages).containsExactlyInAnyOrderElementsOf(expectedErrorMessages);
                });
    }
}
