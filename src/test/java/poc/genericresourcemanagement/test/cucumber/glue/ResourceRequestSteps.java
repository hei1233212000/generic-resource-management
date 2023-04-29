package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourceRequestPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;
import poc.genericresourcemanagement.test.cucumber.common.Verifications;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SuppressWarnings("unused")
public class ResourceRequestSteps implements En {
    private Response response;

    public ResourceRequestSteps(
            final ResourceRequestRepository resourceRequestRepository,
            final ObjectMapper objectMapper,
            final TimeGenerator timeGenerator
    ) {
        Given("there is no resource request exist", () -> assertThat(resourceRequestRepository.count().block())
                .isZero());
        Given("I create a {resourceStatus} {resourceType} resource request {string} in DB with content",
                (ResourceRequestDomainModel.ResourceRequestStatus resourceRequestStatus, ResourceRequestDomainModel.ResourceType resourceType, String requestId, String requestContent) -> {
                    final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
                    final long id = Long.parseLong(requestId);
                    final ResourceRequestPersistenceEntity resourceRequestPersistenceEntity = ResourceRequestPersistenceEntity.builder()
                            .type(resourceType)
                            .id(id)
                            .content(objectMapper.readTree(requestContent))
                            .reason("for testing")
                            .operation(ResourceRequestDomainModel.ResourceRequestOperation.CREATE)
                            .status(resourceRequestStatus)
                            .createdBy("testStep")
                            .createdTime(currentLocalDateTime)
                            .updatedBy("testStep")
                            .updatedTime(currentLocalDateTime)
                            .build();
                    resourceRequestRepository.save(resourceRequestPersistenceEntity).block();
                    final ResourceRequestPersistenceEntity
                            resource = resourceRequestRepository.findByTypeAndId(resourceType, id).block();
                    assertThat(resource)
                            .as("resource should be created")
                            .isNotNull();
                });

        When("I query {resourceType} resource by request id {string}",
                (ResourceRequestDomainModel.ResourceType resourceType, String requestId) -> response = when()
                        .get("/resources/{resourceType}/{requestId}", resourceType, requestId));
        When("I query all {resourceType} resource requests", (ResourceRequestDomainModel.ResourceType resourceType) -> response = when()
                .get("/resources/{resourceType}/", resourceType)
                .then()
                .statusCode(200)
                .extract().response());
        When("I fire the create {resourceType} resource request as",
                (ResourceRequestDomainModel.ResourceType resourceType, String requestBody) -> response = given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/resources/{resourceType}/", resourceType));
        When("I {} the {resourceType} resource request {string}",
                (String requestType, ResourceRequestDomainModel.ResourceType resourceType, String requestId) -> response = given()
                        .post("/resources/{resourceType}/{requestId}/{requestType}", resourceType, requestId, requestType));

        Then("the resource response is an empty array", () -> {
            final JsonNode jsonNode = objectMapper.readTree(response.body().asString());
            assertThat(jsonNode.isArray())
                    .as("should be an array")
                    .isTrue();
            assertThat(jsonNode).isEmpty();
        });
        Then("the resource request is (successfully processed)(failed) with http status code {int}",
                (Integer expectedHttpStatusCode) -> response.then().statusCode(expectedHttpStatusCode));
        Then("the query/approve {resourceType} request response by request id {string} should contain the base info:",
                (ResourceRequestDomainModel.ResourceType resourceType, String requestId, DataTable dataTable) -> {
                    response = when().get("/resources/{resourceType}/{requestId}", resourceType, requestId);
                    final JsonNode actualResult = objectMapper.readTree(response.body().asString());
                    Verifications.verifyJsonNode(dataTable, actualResult);
                });
        Then("the content of the resource response should be:", (DataTable dataTable) -> {
            final JsonNode responseNode = objectMapper.readTree(response.body().asString());
            final JsonNode actualResult = responseNode.get("content");
            Verifications.verifyJsonNode(dataTable, actualResult);
        });
        Then("I got the error messages:", (DataTable dataTable) -> {
            final List<String> expectedErrorMessages = dataTable.asList();

            final JsonNode responseNode = objectMapper.readTree(response.body().asString());
            final List<String> actualErrorMessages = objectMapper.convertValue(
                    responseNode.get("errorMessages"),
                    new TypeReference<>() {}
            );

            assertThat(actualErrorMessages).containsExactlyInAnyOrderElementsOf(expectedErrorMessages);
        });

        ParameterType("resourceType", ".*",
                (String resourceType) -> ResourceRequestDomainModel.ResourceType.valueOf(resourceType));
        ParameterType("resourceStatus", ".*",
                (String resourceStatus) -> ResourceRequestDomainModel.ResourceRequestStatus.valueOf(resourceStatus));
    }
}
