package poc.genericresourcemanagement.interfaces.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateResourceRequestDto(
        @Schema(ref = "CreateResourceRequestContent")
        JsonNode content,
        String reason
) {
}
