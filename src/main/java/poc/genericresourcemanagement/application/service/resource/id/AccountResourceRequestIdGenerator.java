package poc.genericresourcemanagement.application.service.resource.id;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AccountResourceRequestIdGenerator implements ResourceRequestIdGenerator {
    private final ResourceRequestRepository resourceRequestRepository;

    @Override
    public boolean isSupported(final ResourceType resourceType) {
        return resourceType == ResourceType.ACCOUNT;
    }

    @Override
    public Mono<Long> generateResourceRequestId() {
        return resourceRequestRepository.findAccountResourceRequestNextId();
    }
}
