package poc.genericresourcemanagement.interfaces.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;

import java.time.LocalDateTime;

@Builder
public record ResourceRequestDto(
        ResourceType type,
        Long id,

        @Schema(ref = "ResourceContent")
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
