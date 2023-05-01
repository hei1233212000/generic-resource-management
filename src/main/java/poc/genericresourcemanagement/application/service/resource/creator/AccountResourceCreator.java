package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.application.service.common.AccountComponent;
import poc.genericresourcemanagement.infrastructure.persistence.model.AccountPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;
import reactor.core.publisher.Mono;

@Log4j2
public class AccountResourceCreator
        extends AbstractResourceCreator<AccountPersistenceEntity>
        implements AccountComponent {
    private final AccountRepository accountRepository;

    public AccountResourceCreator(final ObjectMapper objectMapper, final AccountRepository accountRepository) {
        super(objectMapper);
        this.accountRepository = accountRepository;
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
