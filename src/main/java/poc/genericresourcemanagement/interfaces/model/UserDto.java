package poc.genericresourcemanagement.interfaces.model;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String name,
        Integer age,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) implements ResourceDto {
}
