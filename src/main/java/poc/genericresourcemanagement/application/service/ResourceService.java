package poc.genericresourcemanagement.application.service;

import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import reactor.core.publisher.Mono;

public class ResourceService {
    public Mono<ResourceDomainModel> findResourceDomainModelById(final String id) {
        return Mono.just(new ResourceDomainModel(id, "{}"));
    }
}
