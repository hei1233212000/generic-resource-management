package poc.genericresourcemanagement.test.cucumber.glue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.infrastructure.persistence.model.UserPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static poc.genericresourcemanagement.test.cucumber.common.Verifications.verifyJsonNode;

@Log4j2
@SuppressWarnings("unused")
public class UserSteps implements En {
    public UserSteps(
            final UserRepository userRepository,
            final ObjectMapper objectMapper
    ) {
        Given("there is no USER exists", () -> assertThat(userRepository.count().block()).isZero());

        Then("the USER {string} is persisted into the database with details:",
                (String username, DataTable dataTable) -> {
                    final UserPersistenceEntity user = userRepository.findByName(username).block();
                    log.info("user: {}", user);
                    assertThat(user).isNotNull();
                    verifyJsonNode(dataTable, objectMapper.valueToTree(user));
                });
    }
}
