package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.infrastructure.persistence.model.AbstractPersistenceEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public abstract class AbstractResourceCreator<R extends AbstractPersistenceEntity> implements ResourceCreator {
    private final ObjectMapper objectMapper;

    protected abstract Class<R> getResourcePersistenceEntityClass();

    protected abstract Mono<Boolean> create(R resource);

    @Override
    public final Mono<Boolean> create(final JsonNode content, final String createdBy, final LocalDateTime createdTime) {
        final R resource = objectMapper.convertValue(content, getResourcePersistenceEntityClass());
        resource.setCreatedBy(createdBy);
        resource.setCreatedTime(createdTime);
        resource.setUpdatedBy(createdBy);
        resource.setUpdatedTime(createdTime);
        return create(resource);
    }
}
