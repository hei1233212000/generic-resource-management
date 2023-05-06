package poc.genericresourcemanagement.application.service.resource.finder;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.common.AccountComponent;
import poc.genericresourcemanagement.domain.model.AccountDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.AccountPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public class AccountResourceFinder implements ResourceFinder<AccountDomainModel>, AccountComponent {
    private final AccountRepository accountRepository;

    @Override
    public Flux<AccountDomainModel> findResources() {
        return accountRepository.findAll()
                .map(entity2DomainModel());
    }

    @Override
    public Mono<AccountDomainModel> findResource(final ResourceType resourceType, final String id) {
        // TODO: we need to validate the id first
        final UUID uuid = UUID.fromString(id);
        return accountRepository.findById(uuid)
                .map(entity2DomainModel());
    }

    private static Function<AccountPersistenceEntity, AccountDomainModel> entity2DomainModel() {
        return resource -> new AccountDomainModel(
                resource.getId(),
                resource.getHolder(),
                resource.getAmount(),
                resource.getVersion(),
                resource.getCreatedBy(),
                resource.getCreatedTime(),
                resource.getUpdatedBy(),
                resource.getUpdatedTime()
        );
    }
}
