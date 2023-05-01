package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import poc.genericresourcemanagement.application.service.common.ResourceSpecificComponent;

public interface ResourceValidator extends ResourceSpecificComponent {
    void validate(final JsonNode resource);
}
