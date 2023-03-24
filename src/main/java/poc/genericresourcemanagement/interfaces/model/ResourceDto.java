package poc.genericresourcemanagement.interfaces.model;

import lombok.Builder;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

import java.time.LocalDateTime;

@Builder
public record ResourceDto(
        ResourceDomainModel.ResourceType type,
        String id,
        String content,
        ResourceDomainModel.ResourceStatus status,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) {
}
