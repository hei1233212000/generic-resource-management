package poc.genericresourcemanagement.infrastructure.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "ACCOUNT")
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AccountPersistenceEntity extends AbstractPersistenceEntity {
    @Id
    @SearchableField
    @SortableField
    private UUID id;

    @Column
    @SearchableField
    @SortableField
    private String holder;

    @Column
    @SearchableField
    @SortableField
    private BigDecimal amount;
}
