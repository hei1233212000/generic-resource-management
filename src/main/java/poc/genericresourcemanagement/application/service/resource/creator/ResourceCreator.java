package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.JsonNode;
import poc.genericresourcemanagement.application.service.common.ResourceSpecificComponent;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ResourceCreator extends ResourceSpecificComponent {
    Mono<Boolean> create(final JsonNode content, final String createdBy, final LocalDateTime createdTime);
}
