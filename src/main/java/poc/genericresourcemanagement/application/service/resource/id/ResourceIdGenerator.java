package poc.genericresourcemanagement.application.service.resource.id;

import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import reactor.core.publisher.Mono;

public interface ResourceIdGenerator {
    boolean isSupported(final ResourceDomainModel.ResourceType resourceType);

    Mono<Long> generateResourceId();
}
