package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

public interface ResourceValidator {
    boolean isSupported(final ResourceDomainModel.ResourceType resourceType);

    void validate(final JsonNode resource);
}
