package poc.genericresourcemanagement.application.service.common;

import poc.genericresourcemanagement.domain.model.ResourceType;

public interface ResourceSpecificComponent {
    default boolean isSupported(final ResourceType resourceType) {
        return resourceType == resourceType();
    }

    ResourceType resourceType();
}
