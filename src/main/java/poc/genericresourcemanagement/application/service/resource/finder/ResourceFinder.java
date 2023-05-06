package poc.genericresourcemanagement.application.service.resource.finder;

import poc.genericresourcemanagement.application.service.common.ResourceSpecificComponent;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import reactor.core.publisher.Flux;

public interface ResourceFinder<DOMAIN extends ResourceDomainModel> extends ResourceSpecificComponent {
    Flux<DOMAIN> findResources();
}
