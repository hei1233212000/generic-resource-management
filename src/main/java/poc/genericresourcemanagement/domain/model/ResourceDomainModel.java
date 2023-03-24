package poc.genericresourcemanagement.domain.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResourceDomainModel(
        ResourceType type,
        String id,
        String content,
        ResourceDomainModel.ResourceStatus status,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) {
    public enum ResourceType {
        USER
    }

    public enum ResourceStatus {
        PENDING_APPROVAL, APPROVED, REJECTED, CANCELLED
    }
}
