package poc.genericresourcemanagement.application.service.resource.validation;

import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

public interface ResourceCreationValidator {
    boolean isSupported(final ResourceDomainModel.ResourceType resourceType);

    void validate(final CreateResourceRequest createResourceRequest);
}
