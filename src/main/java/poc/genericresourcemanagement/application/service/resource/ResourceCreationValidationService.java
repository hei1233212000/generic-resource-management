package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.service.resource.validation.ResourceCreationValidator;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
public class ResourceCreationValidationService {
    private final List<ResourceCreationValidator> validators;

    public void validate(
            final CreateResourceRequest createResourceRequest
    ) {
        validators.stream()
                .filter(validator -> validator.isSupported(createResourceRequest.type()))
                .forEach(validator -> validator.validate(createResourceRequest));
    }
}
