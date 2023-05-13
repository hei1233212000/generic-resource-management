package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourceRequestPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;
import poc.genericresourcemanagement.interfaces.model.PageableDto;
import poc.genericresourcemanagement.interfaces.model.ResourceRequestDto;
import poc.genericresourcemanagement.test.cucumber.common.Verifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static poc.genericresourcemanagement.interfaces.config.LocalDateTimeJsonSerializer.DATE_TIME_FORMATTER;
import static poc.genericresourcemanagement.test.cucumber.common.Verifications.verifyPageableInfo;

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
        Given("I create a {resourceRequestStatus} {resourceType} resource request {string} in DB with content",
                (ResourceRequestDomainModel.ResourceRequestStatus resourceRequestStatus, ResourceType resourceType, String requestId, String requestContent) -> {
                    final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
                    final long id = Long.parseLong(requestId);
                    final ResourceRequestPersistenceEntity resourceRequestPersistenceEntity =
                            ResourceRequestPersistenceEntity.builder()
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
        Given("the below resource requests are already exist:",
                (final DataTable dataTable) ->
                {
                    final List<ResourceRequestPersistenceEntity> resourceRequestPersistenceEntities =
                            dataTable.asMaps().stream()
                                    .map(data -> toResourceRequestPersistenceEntity(objectMapper, data))
                                    .collect(Collectors.toList());
                    resourceRequestRepository.saveAll(resourceRequestPersistenceEntities).blockLast();
                }
        );

        When("I query {resourceType} resource by request id {string}",
                (ResourceType resourceType, String requestId) -> response = when()
                        .get("/resource-requests/{resourceType}/{requestId}", resourceType, requestId));
        When("I query all {resourceType} resource requests", (ResourceType resourceType) -> response = when()
                .get("/resource-requests/{resourceType}/", resourceType)
                .then()
                .statusCode(200)
                .extract().response());
        When("I fire the create {resourceType} resource request as",
                (ResourceType resourceType, String requestBody) -> response = given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/resource-requests/{resourceType}/", resourceType));
        When("I {} the {resourceType} resource request {string}",
                (String requestType, ResourceType resourceType, String requestId) -> response = given()
                        .post("/resource-requests/{resourceType}/{requestId}/{requestType}", resourceType, requestId,
                                requestType)
        );
        When("I query {resourceType} resource requests by using query parameters {string}",
                (ResourceType resourceType, String queryParameters) -> {
                    final String path = "/resource-requests/{resourceType}" +
                            (queryParameters.isBlank() ? "" : "?" + queryParameters);
                    response = given().get(path, resourceType);
                }
        );

        Then("the resource response is an empty array", () -> {
            final PageableDto<ResourceRequestDto> pageableDto = response.body().as(new TypeRef<>() {});
            assertThat(pageableDto).isNotNull();
            assertThat(pageableDto.getData()).isEmpty();
        });
        Then("the resource request is (successfully processed)(failed) with http status code {int}",
                (Integer expectedHttpStatusCode) -> response.then().statusCode(expectedHttpStatusCode));
        Then("the query/approve {resourceType} request response by request id {string} should contain the base info:",
                (ResourceType resourceType, String requestId, DataTable dataTable) -> {
                    response = when().get("/resource-requests/{resourceType}/{requestId}", resourceType, requestId);
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
        Then("I would get the {resourceType} resource requests with id {intList}",
                (ResourceType resourceType, List<String> expectedIds) -> {
                    final PageableDto<ResourceRequestDto> pageableDto = response.body().as(new TypeRef<>() {});
                    final List<String> actualIds = new ArrayList<>();
                    pageableDto.getData().forEach(resourceRequestDto -> {
                        final ResourceType actualType = resourceRequestDto.type();
                        assertThat(actualType)
                                .as("the resourceRequestDto is not in %s type - %s", resourceType, resourceRequestDto)
                                .isEqualTo(resourceType);
                        actualIds.add(resourceRequestDto.id().toString());
                    });
                    assertThat(actualIds).containsExactlyElementsOf(expectedIds);
                }
        );
        Then("I would get the resource requests with below pagination info:", (DataTable dataTable) -> verifyPageableInfo(response, dataTable));

        ParameterType("resourceRequestStatus", ".*",
                (String resourceRequestStatus) -> ResourceRequestDomainModel.ResourceRequestStatus.valueOf(
                        resourceRequestStatus)
        );
    }

    @SneakyThrows
    private ResourceRequestPersistenceEntity toResourceRequestPersistenceEntity(
            final ObjectMapper objectMapper,
            final Map<String, String> data
    ) {
        return ResourceRequestPersistenceEntity.builder()
                .type(data.containsKey("type") ? ResourceType.valueOf(data.get("type")) : null)
                .id(data.containsKey("id") ? Long.parseLong(data.get("id")) : null)
                .content(data.containsKey("content") ? objectMapper.readTree(data.get("content")) : null)
                .reason(data.getOrDefault("reason", null))
                .operation(data.containsKey("operation") ?
                        ResourceRequestDomainModel.ResourceRequestOperation.valueOf(data.get("operation")) :
                        null)
                .status(data.containsKey("status") ?
                        ResourceRequestDomainModel.ResourceRequestStatus.valueOf(data.get("status")) :
                        null)
                .createdBy(data.getOrDefault("createdBy", null))
                .createdTime(data.containsKey("createdTime") ?
                        LocalDateTime.parse(data.get("createdTime"), DATE_TIME_FORMATTER) :
                        null)
                .updatedBy(data.getOrDefault("updatedBy", null))
                .updatedTime(data.containsKey("updatedTime") ?
                        LocalDateTime.parse(data.get("updatedTime"), DATE_TIME_FORMATTER) :
                        null)
                .build();
    }
}
