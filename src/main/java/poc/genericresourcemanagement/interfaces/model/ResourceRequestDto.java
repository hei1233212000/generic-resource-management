package poc.genericresourcemanagement.interfaces.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;

import java.time.LocalDateTime;

@Builder
public record ResourceRequestDto(
        ResourceRequestDomainModel.ResourceType type,
        Long id,
        JsonNode content,
        String reason,
        ResourceRequestDomainModel.ResourceRequestOperation operation,
        ResourceRequestDomainModel.ResourceRequestStatus status,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) {
}
