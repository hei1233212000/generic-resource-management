package poc.genericresourcemanagement.infrastructure.persistence.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

import java.io.Serializable;
import java.time.LocalDateTime;

@Table(name = "RESOURCE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourcePersistenceEntity {

    @Column
    private ResourceDomainModel.ResourceType type;

    @Column
    private Long id;

    @Column
    private JsonNode content;

    @Column
    private ResourceDomainModel.ResourceStatus status;

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

    /**
     * we need this <a href="https://github.com/spring-projects/spring-data-relational/issues/574">ticket</a> to be completed
     * in order to use composite key
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResourcePersistenceEntityPk implements Serializable {

        @Column
        private ResourceDomainModel.ResourceType type;

        @Column
        private String id;
    }
}
