package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.id.ResourceIdGenerator;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ResourceIdGeneratorService {
    private final List<ResourceIdGenerator> resourceIdGenerators;

    public Mono<Long> generateResourceId(final ResourceDomainModel.ResourceType resourceType) {
        final ResourceIdGenerator resourceIdGenerator = resourceIdGenerators.stream()
                .filter(r -> r.isSupported(resourceType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("cannot find the resource id generator for " + resourceType)
                );
        return resourceIdGenerator.generateResourceId();
    }
}
