package poc.genericresourcemanagement.application.service.resource.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import poc.genericresourcemanagement.application.error.ValidationErrorException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Validations {
    public static void validateAndThrowException(final Validator beanValidator, final Object bean) {
        final List<String> errorMessages = validate(beanValidator, bean);
        if(!errorMessages.isEmpty()) {
            throw new ValidationErrorException(errorMessages);
        }
    }

    private static List<String> validate(final Validator beanValidator, final Object bean) {
        return beanValidator.validate(bean)
                .stream().map(Validations.generateValidationErrorFunction())
                .collect(Collectors.toList());
    }

    private static <T> Function<ConstraintViolation<T>, String> generateValidationErrorFunction() {
        return cv -> String.format("'%s' %s", cv.getPropertyPath(), cv.getMessage());
    }
}
