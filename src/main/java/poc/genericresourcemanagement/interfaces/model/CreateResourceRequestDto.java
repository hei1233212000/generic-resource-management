package poc.genericresourcemanagement.interfaces.model;

import com.fasterxml.jackson.databind.JsonNode;

public record CreateResourceRequestDto(
        JsonNode content
) {
}
