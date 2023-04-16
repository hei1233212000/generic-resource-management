package poc.genericresourcemanagement.interfaces.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import poc.genericresourcemanagement.interfaces.rest.ResourceRouter;
import poc.genericresourcemanagement.interfaces.rest.error.ErrorHandlingFunction;

@Import({ResourceRouter.class})
public class ResourceInterfaceConfig {
    @Bean
    // give it a higher priority than the DefaultErrorWebExceptionHandler
    @Order(-2)
    ErrorHandlingFunction errorHandlingFunction(
            final ErrorAttributes errorAttributes,
            final WebProperties webProperties,
            final ApplicationContext applicationContext,
            final ServerCodecConfigurer serverCodecConfigurer
    ) {
        return new ErrorHandlingFunction(errorAttributes, webProperties.getResources(), applicationContext, serverCodecConfigurer);
    }
}
