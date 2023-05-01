package poc.genericresourcemanagement.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import poc.genericresourcemanagement.domain.model.ResourceType;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomResourceRequestRepositoryImpl implements CustomResourceRequestRepository {
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Long> findNextResourceRequestId(final ResourceType resourceType) {
        return databaseClient.sql(queryNextIdSql(resourceType))
                .fetch()
                .first()
                .map(r -> r.values().stream()
                        .map(Object::toString)
                        .findFirst()
                        .map(Long::parseLong)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "fail to find the next resource request id for " + resourceType)
                        )
                );
    }

    private String queryNextIdSql(final ResourceType resourceType) {
        return String.format("SELECT NEXTVAL('%s_RESOURCE_REQUEST_ID_SEQ')", resourceType.name());
    }
}
