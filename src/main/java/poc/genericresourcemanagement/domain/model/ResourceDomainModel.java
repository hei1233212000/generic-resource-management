package poc.genericresourcemanagement.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResourceDomainModel(
        ResourceType type,
        Long id,
        JsonNode content,
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
