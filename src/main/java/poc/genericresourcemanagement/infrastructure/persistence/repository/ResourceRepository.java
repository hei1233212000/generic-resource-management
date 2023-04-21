package poc.genericresourcemanagement.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourcePersistenceEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ResourceRepository extends
        R2dbcRepository<ResourcePersistenceEntity, ResourcePersistenceEntity.ResourcePersistenceEntityPk> {

    Flux<ResourcePersistenceEntity> findAllByType(final ResourceDomainModel.ResourceType type);

    // TODO: we need this because composite key still not suppoerted in Spring R2dbc
    Mono<ResourcePersistenceEntity> findByTypeAndId(final ResourceDomainModel.ResourceType type, final long id);

    @Query(value = "SELECT nextval('USER_REQUEST_ID_SEQ')")
    Mono<Long> findUserResourceNextId();

    // TODO: we need this because composite key still not suppoerted in Spring R2dbc
    @Modifying
    @Query("UPDATE RESOURCE r SET r.STATUS = :newStatus, r.UPDATED_TIMESTAMP = :updatedTime, r.UPDATED_BY = :updatedBy, r.VERSION = :currentVersion + 1 WHERE r.TYPE = :type AND r.ID = :id AND r.VERSION = :currentVersion")
    Mono<Integer> updateStatus(
            @Param("type") final ResourceDomainModel.ResourceType type,
            @Param("id") final long id,
            @Param("currentVersion") final long currentVersion,
            @Param("newStatus") final ResourceDomainModel.ResourceStatus newStatus,
            @Param("updatedBy") final String updatedBy,
            @Param("updatedTime") final LocalDateTime updatedTime
    );
}
