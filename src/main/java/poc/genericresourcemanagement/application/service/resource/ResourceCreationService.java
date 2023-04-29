package poc.genericresourcemanagement.application.service.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.creator.ResourceCreator;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ResourceCreationService {
    private final List<ResourceCreator> resourceCreators;

    public Mono<Boolean> create(
            final ResourceDomainModel.ResourceType resourceType,
            final JsonNode content,
            final String user,
            final LocalDateTime currentLocalDateTime
    ) {
        final ResourceCreator resourceCreator = resourceCreators.stream()
                .filter(r -> r.isSupported(resourceType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("cannot find the resource creator for " + resourceType)
                );
        return resourceCreator.create(content, user, currentLocalDateTime);
    }
}
