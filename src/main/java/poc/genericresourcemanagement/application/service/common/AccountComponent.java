package poc.genericresourcemanagement.application.service.common;

import poc.genericresourcemanagement.domain.model.ResourceType;

public interface AccountComponent extends ResourceSpecificComponent {
    @Override
    default ResourceType resourceType() {
        return ResourceType.ACCOUNT;
    }
}
