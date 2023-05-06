package poc.genericresourcemanagement.application.service.resource.finder;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.common.AccountComponent;
import poc.genericresourcemanagement.domain.model.AccountDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class AccountResourceFinder implements ResourceFinder<AccountDomainModel>, AccountComponent {
    private final AccountRepository userRepository;

    @Override
    public Flux<AccountDomainModel> findResources() {
        return userRepository.findAll()
                .map(resource -> new AccountDomainModel(
                        resource.getId(),
                        resource.getHolder(),
                        resource.getAmount(),
                        resource.getVersion(),
                        resource.getCreatedBy(),
                        resource.getCreatedTime(),
                        resource.getUpdatedBy(),
                        resource.getUpdatedTime()
                ));
    }
}
