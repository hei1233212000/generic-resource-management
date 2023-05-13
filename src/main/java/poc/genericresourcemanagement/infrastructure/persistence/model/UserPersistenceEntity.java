package poc.genericresourcemanagement.infrastructure.persistence.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "MY_USER")
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserPersistenceEntity extends AbstractPersistenceEntity {
    @Id
    @SearchableField
    @SortableField
    private Long id;

    @Column
    @SearchableField
    @SortableField
    private String name;

    @Column
    @SearchableField
    @SortableField
    private Integer age;
}
