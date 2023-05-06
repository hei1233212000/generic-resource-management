package poc.genericresourcemanagement.interfaces.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDto(
        UUID id,
        String holder,
        BigDecimal amount,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) implements ResourceDto {
}
