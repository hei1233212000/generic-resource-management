package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;

@RequiredArgsConstructor
@Log4j2
public class ResourceRequestCreationValidationService {
    private final BeanValidationService beanValidationService;
    private final ResourceRequestValidationService resourceRequestValidationService;

    public void validate(
            final CreateResourceRequest createResourceRequest
    ) {
        beanValidationService.validate(createResourceRequest);
        resourceRequestValidationService.validate(createResourceRequest.type(), createResourceRequest.content());
    }
}
