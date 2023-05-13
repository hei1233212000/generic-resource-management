package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.infrastructure.persistence.model.UserPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;
import poc.genericresourcemanagement.interfaces.model.PageableDto;
import poc.genericresourcemanagement.interfaces.model.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static poc.genericresourcemanagement.interfaces.config.LocalDateTimeJsonSerializer.DATE_TIME_FORMATTER;
import static poc.genericresourcemanagement.test.cucumber.common.Verifications.verifyJsonNode;
import static poc.genericresourcemanagement.test.cucumber.common.Verifications.verifyPageableInfo;

@Log4j2
@SuppressWarnings("unused")
public class UserSteps implements En {
    private Response response;

    public UserSteps(
            final UserRepository userRepository,
            final ObjectMapper objectMapper
    ) {
        Given("there is no USER exists", () -> assertThat(userRepository.count().block()).isZero());
        Given("the below USERs are already exist:",
                (final DataTable dataTable) ->
                {
                    final List<UserPersistenceEntity> userPersistenceEntities =
                            dataTable.asMaps().stream()
                                    .map(data -> toUserPersistenceEntity(objectMapper, data))
                                    .collect(Collectors.toList());
                    userRepository.saveAll(userPersistenceEntities).blockLast();
                });

        When("I query USERs by using query parameters {string}", (String queryParameters) -> {
            final String path = "/resources/USER/" + (queryParameters.isBlank() ? "" : "?" + queryParameters);
            response = given().get(path);
        });

        Then("the USER {string} is persisted into the database with details:",
                (String username, DataTable dataTable) -> {
                    final UserPersistenceEntity user = userRepository.findByName(username).block();
                    log.info("user: {}", user);
                    assertThat(user).isNotNull();
                    verifyJsonNode(dataTable, objectMapper.valueToTree(user));
                });
        Then("I would get the USERs with id {intList}", (List<String> expectedIds) -> {
            final PageableDto<UserDto> pageableDto = response.body().as(new TypeRef<>() {});
            final List<String> actualIds = new ArrayList<>();
            pageableDto.getData().forEach(userDto -> actualIds.add(userDto.id().toString()));
            assertThat(actualIds).containsExactlyElementsOf(expectedIds);
        });
        Then("I would get the USERs with below pagination info:", (DataTable dataTable) -> verifyPageableInfo(response, dataTable));
    }

    private UserPersistenceEntity toUserPersistenceEntity(
            final ObjectMapper objectMapper,
            final Map<String, String> data
    ) {
        return UserPersistenceEntity.builder()
                .id(data.containsKey("id") ? Long.parseLong(data.get("id")) : null)
                .name(data.get("name"))
                .age(data.containsKey("age") ? Integer.parseInt(data.get("age")) : null)
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
