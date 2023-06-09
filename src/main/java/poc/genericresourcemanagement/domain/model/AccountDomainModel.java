package poc.genericresourcemanagement.domain.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDomainModel(
        @NotNull
        UUID id,
        String holder,
        BigDecimal amount,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) implements ResourceDomainModel {
}
