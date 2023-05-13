package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.infrastructure.persistence.model.AccountPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;
import poc.genericresourcemanagement.interfaces.model.PageableDto;
import poc.genericresourcemanagement.interfaces.model.AccountDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static poc.genericresourcemanagement.interfaces.config.LocalDateTimeJsonSerializer.DATE_TIME_FORMATTER;
import static poc.genericresourcemanagement.test.cucumber.common.Verifications.verifyJsonNode;
import static poc.genericresourcemanagement.test.cucumber.common.Verifications.verifyPageableInfo;

@Log4j2
@SuppressWarnings("unused")
public class AccountSteps implements En {
    private Response response;

    public AccountSteps(
            final AccountRepository accountRepository,
            final ObjectMapper objectMapper
    ) {
        Given("there is no ACCOUNT exists", () -> assertThat(accountRepository.count().block()).isZero());
        Given("the below ACCOUNTs are already exist:",
                (final DataTable dataTable) ->
                {
                    final List<AccountPersistenceEntity> accountPersistenceEntities =
                            dataTable.asMaps().stream()
                                    .map(data -> toAccountPersistenceEntity(objectMapper, data))
                                    .collect(Collectors.toList());
                    accountRepository.saveAll(accountPersistenceEntities).blockLast();
                });

        When("I query ACCOUNTs by using query parameters {string}", (String queryParameters) -> {
            final String path = "/resources/ACCOUNT/" + (queryParameters.isBlank() ? "" : "?" + queryParameters);
            response = given().get(path);
        });

        Then("the ACCOUNT {string} is persisted into the database with details:",
                (String accountId, DataTable dataTable) -> {
                    final AccountPersistenceEntity account =
                            accountRepository.findById(UUID.fromString(accountId)).block();
                    log.info("account: {}", account);
                    assertThat(account).isNotNull();
                    verifyJsonNode(dataTable, objectMapper.valueToTree(account));
                });
        Then("I would get the ACCOUNTs with id {intList}", (List<String> expectedIds) -> {
            final PageableDto<AccountDto> pageableDto = response.body().as(new TypeRef<>() {});
            final List<String> actualIds = new ArrayList<>();
            pageableDto.getData().forEach(accountDto -> actualIds.add(accountDto.id().toString()));
            assertThat(actualIds).containsExactlyElementsOf(expectedIds);
        });
        Then("I would get the ACCOUNTs with below pagination info:", (DataTable dataTable) -> verifyPageableInfo(response, dataTable));
    }

    private AccountPersistenceEntity toAccountPersistenceEntity(
            final ObjectMapper objectMapper,
            final Map<String, String> data
    ) {
        return AccountPersistenceEntity.builder()
                .id(data.containsKey("id") ? UUID.fromString(data.get("id")) : null)
                .holder(data.get("holder"))
                .amount(data.containsKey("amount") ? new BigDecimal(data.get("amount")) : null)
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
