package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SuppressWarnings("unused")
public class ManageResourceSteps implements En {
    private Response response;

    public ManageResourceSteps(
            final ResourceRepository resourceRepository,
            final ObjectMapper objectMapper
    ) {
        Given("there is no resource exist", () -> assertThat(resourceRepository.count().block())
                .isZero());

        When("query all {resourceType} resources", (ResourceDomainModel.ResourceType resourceType) -> response = when()
                .get("/resources/{resourceType}/", resourceType)
                .then()
                .statusCode(200)
                .extract().response());
        When("I fire the create {resourceType} resource request as",
                (ResourceDomainModel.ResourceType resourceType, String requestBody) -> response = given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/resources/{resourceType}/", resourceType));

        Then("the resource response is an empty array", () -> {
            final JsonNode jsonNode = objectMapper.readTree(response.body().asString());
            assertThat(jsonNode.isArray())
                    .as("should be an array")
                    .isTrue();
            assertThat(jsonNode).isEmpty();
        });
        Then("the resource request is successfully processed", () -> response.then().statusCode(201));
        Then("the {resourceType} response of with id {string} should contain the base info:",
                (ResourceDomainModel.ResourceType resourceType, String requestId, DataTable dataTable) -> {
                    response = when().get("/resources/{resourceType}/{requestId}", resourceType, requestId);
                    final JsonNode actualResult = objectMapper.readTree(response.body().asString());
                    verifyJsonNode(dataTable, actualResult);
                });
        Then("the content of the resource response should be:", (DataTable dataTable) -> {
            final JsonNode responseNode = objectMapper.readTree(response.body().asString());
            final JsonNode actualResult = responseNode.get("content");
            verifyJsonNode(dataTable, actualResult);
        });

        ParameterType("resourceType", ".*",
                (String resourceType) -> ResourceDomainModel.ResourceType.valueOf(resourceType));
    }

    private static void verifyJsonNode(final DataTable dataTable, final JsonNode actualResult) {
        final Map<String, String> expectedResultMap = dataTable.asMap();
        expectedResultMap.forEach((fieldName, expectedValue) -> {
            assertThat(actualResult.has(fieldName))
                    .as("the response body does not contain \"%s\"", fieldName)
                    .isTrue();
            assertThat(actualResult.get(fieldName).asText()).isEqualTo(expectedResultMap.get(fieldName));
        });
    }
}
