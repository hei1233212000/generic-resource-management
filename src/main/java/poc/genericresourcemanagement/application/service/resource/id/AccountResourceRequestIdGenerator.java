package poc.genericresourcemanagement.application.service.resource.id;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.common.AccountComponent;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AccountResourceRequestIdGenerator implements ResourceRequestIdGenerator, AccountComponent {
    private final ResourceRequestRepository resourceRequestRepository;

    @Override
    public Mono<Long> generateResourceRequestId() {
        return resourceRequestRepository.findAccountResourceRequestNextId();
    }
}
