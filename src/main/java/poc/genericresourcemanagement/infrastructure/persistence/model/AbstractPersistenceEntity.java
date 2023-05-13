package poc.genericresourcemanagement.infrastructure.persistence.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
public class AbstractPersistenceEntity implements PersistenceEntity {
    @Version
    private Long version;

    @Column
    @SearchableField
    private String createdBy;

    @Column("CREATED_TIMESTAMP")
    @SearchableField
    @SortableField
    private LocalDateTime createdTime;

    @Column
    @SearchableField
    private String updatedBy;

    @Column("UPDATED_TIMESTAMP")
    @SearchableField
    @SortableField
    private LocalDateTime updatedTime;
}
