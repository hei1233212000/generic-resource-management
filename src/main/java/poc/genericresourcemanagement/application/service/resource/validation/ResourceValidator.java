package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;

public interface ResourceValidator {
    boolean isSupported(final ResourceRequestDomainModel.ResourceType resourceType);

    void validate(final JsonNode resource);
}
