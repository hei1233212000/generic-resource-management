package poc.genericresourcemanagement.application.service.common;

import poc.genericresourcemanagement.domain.model.ResourceType;

public interface UserComponent extends ResourceSpecificComponent {
    @Override
    default ResourceType resourceType() {
        return ResourceType.USER;
    }
}
