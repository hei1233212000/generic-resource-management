package poc.genericresourcemanagement.application.service.common;

import poc.genericresourcemanagement.domain.model.ResourceType;

public interface ResourceSpecificComponent {
    boolean isSupported(final ResourceType resourceType);
}
