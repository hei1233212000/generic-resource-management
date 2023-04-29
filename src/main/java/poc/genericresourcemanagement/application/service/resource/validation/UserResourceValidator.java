package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.UserDomainModel;

@RequiredArgsConstructor
public class UserResourceValidator implements ResourceValidator {
    private final ObjectMapper objectMapper;
    private final BeanValidationService beanValidationService;

    @Override
    public boolean isSupported(final ResourceRequestDomainModel.ResourceType resourceType) {
        return resourceType == ResourceRequestDomainModel.ResourceType.USER;
    }

    @Override
    public void validate(final JsonNode resource) {
        final UserDomainModel userDomainModel = objectMapper.convertValue(resource, UserDomainModel.class);
        beanValidationService.validate(userDomainModel);
    }
}
