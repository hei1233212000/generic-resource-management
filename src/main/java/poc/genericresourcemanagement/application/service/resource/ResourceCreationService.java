package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.creator.ResourceCreator;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ResourceCreationService {
    private final List<ResourceCreator> resourceCreators;

    public Mono<Boolean> create(final ResourceDomainModel resourceDomainModel) {
        final ResourceDomainModel.ResourceType resourceType = resourceDomainModel.type();
        final ResourceCreator resourceCreator = resourceCreators.stream()
                .filter(r -> r.isSupported(resourceType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("cannot find the resource creator for " + resourceType)
                );
        return resourceCreator.create(
                resourceDomainModel.content(),
                resourceDomainModel.updatedBy(),
                resourceDomainModel.updatedTime()
        );
    }
}
