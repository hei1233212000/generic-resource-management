package poc.genericresourcemanagement.application.service.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.validation.ResourceValidator;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

import java.util.List;

@RequiredArgsConstructor
public class ResourceValidationService {
    private final List<ResourceValidator> resourceValidators;

    public void validate(final ResourceDomainModel.ResourceType resourceType, final JsonNode resource) {
        resourceValidators.stream()
                .filter(validator -> validator.isSupported(resourceType))
                .forEach(validator -> validator.validate(resource));
    }
}
