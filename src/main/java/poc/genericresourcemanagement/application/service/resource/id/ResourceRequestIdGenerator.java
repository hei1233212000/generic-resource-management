package poc.genericresourcemanagement.application.service.resource.id;

import poc.genericresourcemanagement.domain.model.ResourceType;
import reactor.core.publisher.Mono;

public interface ResourceRequestIdGenerator {
    boolean isSupported(final ResourceType resourceType);

    Mono<Long> generateResourceRequestId();
}
