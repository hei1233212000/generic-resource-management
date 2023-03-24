package poc.genericresourcemanagement.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourcePersistenceEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResourceRepository extends
        R2dbcRepository<ResourcePersistenceEntity, ResourcePersistenceEntity.ResourcePersistenceEntityPk> {

    Flux<ResourcePersistenceEntity> findAllByType(final ResourceDomainModel.ResourceType type);

    Mono<ResourcePersistenceEntity> findByTypeAndId(final ResourceDomainModel.ResourceType type, final String id);
}
