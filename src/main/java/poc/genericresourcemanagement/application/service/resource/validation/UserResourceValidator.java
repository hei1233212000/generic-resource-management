package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.error.ValidationErrorException;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserResourceValidator implements ResourceValidator {
    @Override
    public boolean isSupported(final ResourceDomainModel.ResourceType resourceType) {
        return resourceType == ResourceDomainModel.ResourceType.USER;
    }

    @Override
    public void validate(final JsonNode resource) {
        final List<String> errorMessages = new ArrayList<>();
        validateRequiredField(resource, "name", errorMessages);
        validateRequiredField(resource, "age", errorMessages);
        if(!errorMessages.isEmpty()) {
            throw new ValidationErrorException(errorMessages);
        }
    }

    private static void validateRequiredField(
            final JsonNode resource,
            final String field,
            final List<String> errorMessages
    ) {
        if(!resource.has(field)) {
            errorMessages.add(String.format("missing '%s'", field));
        }
    }
}
