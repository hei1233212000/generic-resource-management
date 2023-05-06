package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.test.cucumber.common.Verifications;

import static io.restassured.RestAssured.when;

@Log4j2
@SuppressWarnings("unused")
public class ResourceSteps implements En {
    public ResourceSteps(
            final ObjectMapper objectMapper
    ) {
        Then("query {resourceType} resource by id {string} should return the base info:",
                (ResourceType resourceType, String resourceId, DataTable dataTable) -> {
                    final Response response = when().get("/resources/{resourceType}/{resourceId}", resourceType, resourceId);
                    final JsonNode actualResult = objectMapper.readTree(response.body().asString());
                    Verifications.verifyJsonNode(dataTable, actualResult);
                });
    }
}
