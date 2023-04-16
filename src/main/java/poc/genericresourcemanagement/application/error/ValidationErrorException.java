package poc.genericresourcemanagement.application.error;

import lombok.Getter;

import java.util.List;

public class ValidationErrorException extends RuntimeException {
    @Getter
    private final List<String> messages;

    public ValidationErrorException(final List<String> messages) {
        super();
        this.messages = messages;
    }
}
