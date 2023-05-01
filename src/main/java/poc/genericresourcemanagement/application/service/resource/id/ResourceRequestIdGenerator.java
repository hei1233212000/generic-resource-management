package poc.genericresourcemanagement.application.service.resource.id;

import poc.genericresourcemanagement.application.service.common.ResourceSpecificComponent;
import reactor.core.publisher.Mono;

public interface ResourceRequestIdGenerator extends ResourceSpecificComponent {
    Mono<Long> generateResourceRequestId();
}
