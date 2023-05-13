package poc.genericresourcemanagement.interfaces.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import poc.genericresourcemanagement.application.service.resource.ResourceRequestService;
import poc.genericresourcemanagement.application.service.resource.ResourceService;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.interfaces.model.ResourceDto;
import poc.genericresourcemanagement.interfaces.rest.error.ErrorHandlingFunction;
import poc.genericresourcemanagement.interfaces.rest.handler.ResourceHandler;
import poc.genericresourcemanagement.interfaces.rest.handler.ResourceRequestHandler;
import poc.genericresourcemanagement.interfaces.rest.mapper.ResourceDomainModel2DtoMapper;
import poc.genericresourcemanagement.interfaces.rest.route.ResourceRequestRouter;
import poc.genericresourcemanagement.interfaces.rest.route.ResourceRouter;

import java.util.List;

@Import({
        UserResourceInterfaceConfig.class,
        AccountResourceInterfaceConfig.class,
        ResourceRequestRouter.class,
        ResourceRouter.class,
        OpenApiConfig.class,
})
public class InterfaceConfig {
    @Bean
    // give it a higher priority than the DefaultErrorWebExceptionHandler
    @Order(-2)
    ErrorHandlingFunction errorHandlingFunction(
            final ErrorAttributes errorAttributes,
            final WebProperties webProperties,
            final ApplicationContext applicationContext,
            final ServerCodecConfigurer serverCodecConfigurer
    ) {
        return new ErrorHandlingFunction(errorAttributes, webProperties.getResources(), applicationContext,
                serverCodecConfigurer);
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .serializers(new LocalDateTimeJsonSerializer(), new BigDecimalJsonSerializer());
    }

    @Bean
    ResourceRequestHandler resourceRequestHandler(final ResourceRequestService resourceRequestService) {
        return new ResourceRequestHandler(resourceRequestService);
    }

    @Bean
    ResourceHandler resourceHandler(
            final ResourceService resourceService,
            final List<ResourceDomainModel2DtoMapper<? extends ResourceDomainModel, ? extends ResourceDto>> resourceDomainModel2DtoMappers
    ) {
        return new ResourceHandler(resourceService, resourceDomainModel2DtoMappers);
    }
}
