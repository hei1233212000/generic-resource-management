package poc.genericresourcemanagement.infrastructure.persistence.repository;

import poc.genericresourcemanagement.domain.model.ResourceType;
import reactor.core.publisher.Mono;

public interface CustomResourceRequestRepository {
    Mono<Long> findNextResourceRequestId(final ResourceType resourceType);
}
