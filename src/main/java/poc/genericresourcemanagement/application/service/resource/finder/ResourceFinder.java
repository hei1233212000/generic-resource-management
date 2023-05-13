package poc.genericresourcemanagement.application.service.resource.finder;

import poc.genericresourcemanagement.application.service.common.ResourceSpecificComponent;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.PersistenceEntity;
import reactor.core.publisher.Mono;

public interface ResourceFinder extends ResourceSpecificComponent {
    Mono<ResourceDomainModel> findResource(final ResourceType resourceType, final String id);

    Class<? extends PersistenceEntity> persistenceEntityClass();

    ResourceDomainModel convertPersistenceEntity2DomainModel(PersistenceEntity persistenceEntity);
}
