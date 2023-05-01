package poc.genericresourcemanagement.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import poc.genericresourcemanagement.infrastructure.persistence.model.AccountPersistenceEntity;

import java.util.UUID;

public interface AccountRepository extends R2dbcRepository<AccountPersistenceEntity, UUID> {
}
