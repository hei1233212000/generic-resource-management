package poc.genericresourcemanagement.application.service.resource.id;

import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import reactor.core.publisher.Mono;

public interface ResourceRequestIdGenerator {
    boolean isSupported(final ResourceRequestDomainModel.ResourceType resourceType);

    Mono<Long> generateResourceRequestId();
}
