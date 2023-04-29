package poc.genericresourcemanagement.infrastructure.persistence.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

import java.io.Serializable;

@Table(name = "RESOURCE")
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ResourcePersistenceEntity extends AbstractPersistenceEntity {

    @Column
    private ResourceDomainModel.ResourceType type;

    @Column
    private Long id;

    @Column
    private JsonNode content;

    @Column
    private String reason;

    @Column
    private ResourceDomainModel.Operation operation;

    @Column
    private ResourceDomainModel.ResourceStatus status;

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
        private Long id;
    }
}
