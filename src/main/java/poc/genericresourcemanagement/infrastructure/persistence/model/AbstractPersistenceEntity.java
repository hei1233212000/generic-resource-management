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
public class AbstractPersistenceEntity {
    @Version
    private Long version;

    @Column
    private String createdBy;

    @Column("CREATED_TIMESTAMP")
    private LocalDateTime createdTime;

    @Column
    private String updatedBy;

    @Column("UPDATED_TIMESTAMP")
    private LocalDateTime updatedTime;
}
