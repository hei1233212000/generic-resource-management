package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.JsonNode;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ResourceCreator {
    boolean isSupported(final ResourceDomainModel.ResourceType resourceType);

    Mono<Boolean> create(final JsonNode content, final String createdBy, final LocalDateTime createdTime);
}