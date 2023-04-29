package poc.genericresourcemanagement.interfaces.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

import java.time.LocalDateTime;

@Builder
public record ResourceDto(
        ResourceDomainModel.ResourceType type,
        Long id,
        JsonNode content,
        String reason,
        ResourceDomainModel.Operation operation,
        ResourceDomainModel.ResourceStatus status,
        Long version,
        String createdBy,
        LocalDateTime createdTime,
        String updatedBy,
        LocalDateTime updatedTime
) {
}
