package poc.genericresourcemanagement.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourceRequestPersistenceEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ResourceRequestRepository extends
        R2dbcRepository<ResourceRequestPersistenceEntity, ResourceRequestPersistenceEntity.ResourcePersistenceEntityPk> {

    Flux<ResourceRequestPersistenceEntity> findAllByType(final ResourceType type);

    // TODO: we need this because composite key still not suppoerted in Spring R2dbc
    Mono<ResourceRequestPersistenceEntity> findByTypeAndId(final ResourceType type, final long id);

    @Query(value = "SELECT nextval('USER_RESOURCE_REQUEST_ID_SEQ')")
    Mono<Long> findUserResourceRequestNextId();

    @Query(value = "SELECT nextval('ACCOUNT_RESOURCE_REQUEST_ID_SEQ')")
    Mono<Long> findAccountResourceRequestNextId();

    // TODO: we need this because composite key still not suppoerted in Spring R2dbc
    @Modifying
    @Query("UPDATE RESOURCE_REQUEST r SET r.STATUS = :newStatus, r.UPDATED_TIMESTAMP = :updatedTime, r.UPDATED_BY = :updatedBy, r.VERSION = :currentVersion + 1 WHERE r.TYPE = :type AND r.ID = :id AND r.VERSION = :currentVersion")
    Mono<Integer> updateStatus(
            @Param("type") final ResourceType type,
            @Param("id") final long id,
            @Param("currentVersion") final long currentVersion,
            @Param("newStatus") final ResourceRequestDomainModel.ResourceRequestStatus newStatus,
            @Param("updatedBy") final String updatedBy,
            @Param("updatedTime") final LocalDateTime updatedTime
    );
}
