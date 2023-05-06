package poc.genericresourcemanagement.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record UserDomainModel(
        Long id,

        @NotBlank
        String name,

        @Positive
        @NotNull
        Integer age,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) implements ResourceDomainModel {}
