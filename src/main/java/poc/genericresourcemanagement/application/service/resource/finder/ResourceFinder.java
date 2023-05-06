package poc.genericresourcemanagement.application.service.resource.finder;

import poc.genericresourcemanagement.application.service.common.ResourceSpecificComponent;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResourceFinder<DOMAIN extends ResourceDomainModel> extends ResourceSpecificComponent {
    Flux<DOMAIN> findResources();

    Mono<DOMAIN> findResource(final ResourceType resourceType, final String id);
}
