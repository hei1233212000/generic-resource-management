package poc.genericresourcemanagement.application.service.common;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.resource.validation.Validations;

@RequiredArgsConstructor
public class BeanValidationService {
    private final jakarta.validation.Validator beanValidator;

    public void validate(final Object bean) {
        Validations.validateAndThrowException(beanValidator, bean);
    }
}
