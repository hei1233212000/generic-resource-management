package poc.genericresourcemanagement.application.service.resource.id;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserResourceRequestIdGenerator implements ResourceRequestIdGenerator {
    private final ResourceRequestRepository resourceRequestRepository;

    @Override
    public boolean isSupported(final ResourceRequestDomainModel.ResourceType resourceType) {
        return resourceType == ResourceRequestDomainModel.ResourceType.USER;
    }

    @Override
    public Mono<Long> generateResourceRequestId() {
        return resourceRequestRepository.findUserResourceRequestNextId();
    }
}
