package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.application.service.resource.validation.ResourceCreationValidator;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
public class ResourceCreationValidationService {
    private final BeanValidationService beanValidationService;
    private final List<ResourceCreationValidator> validators;

    @Validated
    public void validate(
            final CreateResourceRequest createResourceRequest
    ) {
        beanValidationService.validate(createResourceRequest);
        validators.stream()
                .filter(validator -> validator.isSupported(createResourceRequest.type()))
                .forEach(validator -> validator.validate(createResourceRequest));
    }
}
