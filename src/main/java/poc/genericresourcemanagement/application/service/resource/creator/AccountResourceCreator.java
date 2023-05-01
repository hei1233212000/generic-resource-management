package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.AccountPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;
import reactor.core.publisher.Mono;

@Log4j2
public class AccountResourceCreator extends AbstractResourceCreator<AccountPersistenceEntity> {
    private final AccountRepository accountRepository;

    public AccountResourceCreator(final ObjectMapper objectMapper, final AccountRepository accountRepository) {
        super(objectMapper);
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean isSupported(final ResourceType resourceType) {
        return resourceType == ResourceType.ACCOUNT;
    }

    @Override
    protected Class<AccountPersistenceEntity> getResourceClass() {
        return AccountPersistenceEntity.class;
    }

    @Override
    protected Mono<Boolean> create(final AccountPersistenceEntity account) {
        return accountRepository.save(account)
                .map(u -> true);
    }
}
