package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.infrastructure.persistence.model.AccountPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static poc.genericresourcemanagement.test.cucumber.common.Verifications.verifyJsonNode;

@Log4j2
@SuppressWarnings("unused")
public class AccountSteps implements En {
    public AccountSteps(
            final AccountRepository accountRepository,
            final ObjectMapper objectMapper
    ) {
        Given("there is no ACCOUNT exists", () -> assertThat(accountRepository.count().block()).isZero());

        Then("the ACCOUNT {string} is persisted into the database with details:",
                (String accountId, DataTable dataTable) -> {
                    final AccountPersistenceEntity account =
                            accountRepository.findById(UUID.fromString(accountId)).block();
                    log.info("account: {}", account);
                    assertThat(account).isNotNull();
                    verifyJsonNode(dataTable, objectMapper.valueToTree(account));
                });
    }
}
