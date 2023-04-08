package poc.genericresourcemanagement.application.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

@Builder
public record CreateResourceRequest(
        ResourceDomainModel.ResourceType type,
        String id,
        JsonNode content,
        String createdBy
) {
}
