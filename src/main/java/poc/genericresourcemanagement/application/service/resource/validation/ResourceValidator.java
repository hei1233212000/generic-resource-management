package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import poc.genericresourcemanagement.domain.model.ResourceType;

public interface ResourceValidator {
    boolean isSupported(final ResourceType resourceType);

    void validate(final JsonNode resource);
}
