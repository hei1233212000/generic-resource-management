package poc.genericresourcemanagement.infrastructure.persistence.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;

import java.io.Serializable;

@Table(name = "RESOURCE_REQUEST")
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ResourceRequestPersistenceEntity extends AbstractPersistenceEntity {

    @Column
    @SearchableField
    private ResourceType type;

    @Column
    @SearchableField
    @SortableField
    private Long id;

    @Column
    private JsonNode content;

    @Column
    @SearchableField
    private String reason;

    @Column
    @SearchableField
    private ResourceRequestDomainModel.ResourceRequestOperation operation;

    @Column
    @SearchableField
    private ResourceRequestDomainModel.ResourceRequestStatus status;

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
        private ResourceType type;

        @Column
        private Long id;
    }
}
