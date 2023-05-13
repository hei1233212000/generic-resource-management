package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.model.Pageable;
import poc.genericresourcemanagement.application.model.Query;
import poc.genericresourcemanagement.application.service.resource.finder.ResourceFinder;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.PersistenceEntity;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ResourceService {
    private final QueryService queryService;
    private final List<ResourceFinder> resourceFinders;

    public Mono<Pageable<ResourceDomainModel>> findResources(
            final ResourceType resourceType,
            final Query query
    ) {
        final ResourceFinder resourceFinder = findResourceFinder(resourceType);
        final Class<? extends PersistenceEntity> persistenceEntityClass = resourceFinder.persistenceEntityClass();
        return queryService.query(
                persistenceEntityClass,
                query,
                resourceFinder::convertPersistenceEntity2DomainModel
        );
    }

    public Mono<? extends ResourceDomainModel> findResource(final ResourceType resourceType, final String id) {
        final ResourceFinder resourceFinder = findResourceFinder(resourceType);
        return resourceFinder.findResource(resourceType, id);
    }

    private ResourceFinder findResourceFinder(
            final ResourceType resourceType
    ) {
        return resourceFinders.stream()
                .filter(r -> r.isSupported(resourceType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("cannot find the resource mapper for " + resourceType)
                );
    }
}
