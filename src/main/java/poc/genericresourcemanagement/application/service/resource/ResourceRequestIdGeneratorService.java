package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.id.ResourceRequestIdGenerator;
import poc.genericresourcemanagement.domain.model.ResourceType;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ResourceRequestIdGeneratorService {
    private final List<ResourceRequestIdGenerator> resourceRequestIdGenerators;

    public Mono<Long> generateResourceRequestId(final ResourceType resourceType) {
        final ResourceRequestIdGenerator resourceRequestIdGenerator = resourceRequestIdGenerators.stream()
                .filter(r -> r.isSupported(resourceType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("cannot find the resource id generator for " + resourceType)
                );
        return resourceRequestIdGenerator.generateResourceRequestId();
    }
}
