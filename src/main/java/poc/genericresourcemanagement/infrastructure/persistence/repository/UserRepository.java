package poc.genericresourcemanagement.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import poc.genericresourcemanagement.infrastructure.persistence.model.UserPersistenceEntity;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserPersistenceEntity, Long> {
    Mono<UserPersistenceEntity> findByName(String username);

    @Query(value = "SELECT nextval('USER_ID_SEQ')")
    Mono<Long> findUserNextId();
}
