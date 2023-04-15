package poc.genericresourcemanagement.application.service.resource.id;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserResourceIdGenerator implements ResourceIdGenerator {
    private final ResourceRepository resourceRepository;

    @Override
    public boolean isSupported(final ResourceDomainModel.ResourceType resourceType) {
        return resourceType == ResourceDomainModel.ResourceType.USER;
    }

    @Override
    public Mono<Long> generateResourceId() {
        return resourceRepository.findUserResourceNextId();
    }
}
