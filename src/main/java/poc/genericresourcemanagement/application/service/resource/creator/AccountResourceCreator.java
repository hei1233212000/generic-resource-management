package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.AccountPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Log4j2
public class AccountResourceCreator implements ResourceCreator {
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    @Override
    public boolean isSupported(final ResourceType resourceType) {
        return resourceType == ResourceType.ACCOUNT;
    }

    @Override
    public Mono<Boolean> create(final JsonNode content, final String createdBy, final LocalDateTime createdTime) {
        final AccountPersistenceEntity account = objectMapper.convertValue(content, AccountPersistenceEntity.class);
        account.setCreatedBy(createdBy);
        account.setCreatedTime(createdTime);
        account.setUpdatedBy(createdBy);
        account.setUpdatedTime(createdTime);
        return accountRepository.save(account)
                .map(u -> true);
    }
}
