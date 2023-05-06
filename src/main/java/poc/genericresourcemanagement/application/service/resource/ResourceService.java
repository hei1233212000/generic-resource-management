package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.finder.ResourceFinder;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class ResourceService {
    private final List<ResourceFinder<? extends ResourceDomainModel>> resourceFinders;

    public Flux<? extends ResourceDomainModel> findResources(final ResourceType resourceType) {
        final ResourceFinder<? extends ResourceDomainModel> resourceFinder = findResourceFinder(resourceType);
        return resourceFinder.findResources();
    }

    private ResourceFinder<? extends ResourceDomainModel> findResourceFinder(final ResourceType resourceType) {
        return resourceFinders.stream()
                .filter(r -> r.isSupported(resourceType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("cannot find the resource mapper for " + resourceType)
                );
    }
}
