package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;

@RequiredArgsConstructor
public abstract class AbstractResourceValidator<R> implements ResourceValidator {
    private final ObjectMapper objectMapper;
    private final BeanValidationService beanValidationService;

    protected abstract Class<R> getResourceDomainModelClass();

    @Override
    public void validate(final JsonNode resource) {
        final R resourceDomainModel = objectMapper.convertValue(resource, getResourceDomainModelClass());
        beanValidationService.validate(resourceDomainModel);
    }
}
