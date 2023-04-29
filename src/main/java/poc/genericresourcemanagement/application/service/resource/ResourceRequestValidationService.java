package poc.genericresourcemanagement.application.service.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.validation.ResourceValidator;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;

import java.util.List;

@RequiredArgsConstructor
public class ResourceRequestValidationService {
    private final List<ResourceValidator> resourceValidators;

    public void validate(final ResourceRequestDomainModel.ResourceType resourceType, final JsonNode resource) {
        resourceValidators.stream()
                .filter(validator -> validator.isSupported(resourceType))
                .forEach(validator -> validator.validate(resource));
    }
}
