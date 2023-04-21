package poc.genericresourcemanagement.application.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

@Builder
public record CreateResourceRequest(
        @NotNull
        ResourceDomainModel.ResourceType type,

        @NotNull
        JsonNode content,

        @NotBlank
        String reason,

        @NotBlank
        String createdBy
) {
}
