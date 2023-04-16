package poc.genericresourcemanagement.application.service.resource.validation;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.error.ValidationErrorException;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserResourceCreationValidator implements ResourceCreationValidator {
    @Override
    public boolean isSupported(final ResourceDomainModel.ResourceType resourceType) {
        return resourceType == ResourceDomainModel.ResourceType.USER;
    }

    @Override
    public void validate(final CreateResourceRequest createResourceRequest) {
        final List<String> errorMessages = new ArrayList<>();
        validateRequiredField(createResourceRequest, "name", errorMessages);
        validateRequiredField(createResourceRequest, "age", errorMessages);
        if(!errorMessages.isEmpty()) {
            throw new ValidationErrorException(errorMessages);
        }
    }

    private static void validateRequiredField(
            final CreateResourceRequest createResourceRequest,
            final String field,
            final List<String> errorMessages
    ) {
        if(!createResourceRequest.content().has(field)) {
            errorMessages.add(String.format("missing '%s'", field));
        }
    }
}
