package poc.genericresourcemanagement.application.service.common;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.error.ValidationErrorException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BeanValidationService {
    private final jakarta.validation.Validator beanValidator;

    public void validate(final Object bean) {
        final List<String> errorMessages = beanValidator.validate(bean)
                .stream().map(cv -> String.format("'%s' %s", cv.getPropertyPath(), cv.getMessage()))
                .collect(Collectors.toList());
        if(!errorMessages.isEmpty()) {
            throw new ValidationErrorException(errorMessages);
        }
    }
}
