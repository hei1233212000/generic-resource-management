package poc.genericresourcemanagement.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResourceRequestDomainModel(
        ResourceType type,
        Long id,
        JsonNode content,
        String reason,
        ResourceRequestOperation operation,
        ResourceRequestStatus status,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) {
    public enum ResourceRequestStatus {
        PENDING_APPROVAL, APPROVED, CANCELLED
    }

    public enum ResourceRequestOperation {
        CREATE
    }
}
