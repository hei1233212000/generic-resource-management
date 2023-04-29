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
    private Long id;

    @Column
    private String name;

    @Column
    private Integer age;
}
