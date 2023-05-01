package poc.genericresourcemanagement.interfaces.rest.error;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import poc.genericresourcemanagement.application.error.FailToChangeResourceRequestStatusException;
import poc.genericresourcemanagement.application.error.ValidationErrorException;
import poc.genericresourcemanagement.interfaces.model.ErrorResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public class ErrorHandlingFunction extends AbstractErrorWebExceptionHandler {
    public ErrorHandlingFunction(
            final ErrorAttributes errorAttributes,
            final WebProperties.Resources resources,
            final ApplicationContext applicationContext,
            final ServerCodecConfigurer serverCodecConfigurer
    ) {
        super(errorAttributes, resources, applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest serverRequest) {
        final HttpStatus httpStatus = determineHttpStatus(serverRequest);
        final ErrorResponseDto errorResponseDto = generateErrorResponseDto(serverRequest);
        return ServerResponse
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponseDto));
    }

    private HttpStatus determineHttpStatus(final ServerRequest serverRequest) {
        final Throwable error = getError(serverRequest);
        if(error instanceof ValidationErrorException) {
            return HttpStatus.BAD_REQUEST;
        }
        if(error instanceof FailToChangeResourceRequestStatusException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ErrorResponseDto generateErrorResponseDto(final ServerRequest serverRequest) {
        final List<String> errorMessages;
        final Throwable error = getError(serverRequest);
        if(error instanceof ValidationErrorException) {
            errorMessages = ((ValidationErrorException) error).getMessages();
        } else if(error instanceof final FailToChangeResourceRequestStatusException exception) {
            errorMessages = List.of(
                    String.format("cannot %s %s resource request with id '%s' because it is in '%s' state",
                            exception.getRequestOperation().name().toLowerCase(), exception.getResourceType(),
                            exception.getResourceRequestId(), exception.getCurrentStatus()
                    )
            );
        } else {
            errorMessages = List.of("Unknown error");
        }
        return new ErrorResponseDto(errorMessages);
    }
}
