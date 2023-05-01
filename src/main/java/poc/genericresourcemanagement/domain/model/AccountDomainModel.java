package poc.genericresourcemanagement.domain.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDomainModel(
        @NotNull
        UUID id,
        String holder,
        BigDecimal amount
) {
}
