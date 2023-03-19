package poc.genericresourcemanagement.interfaces.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import poc.genericresourcemanagement.application.service.ResourceService;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.interfaces.model.ResourceDto;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

public class ResourceRouter {
    @Bean
    RouterFunction<ServerResponse> getPersonRoute(final ResourceService resourceService) {
        return RouterFunctions.route(GET("/resources/{id}"),
                request -> {
                    Mono<ResourceDto> resource =
                            resourceService.findResourceDomainModelById(request.pathVariable("id"))
                                    .map(this::convert);
                    return ServerResponse.ok()
                            .body(BodyInserters.fromPublisher(resource, ResourceDto.class));
                }
        );
    }

    private ResourceDto convert(final ResourceDomainModel resourceDomainModel) {
        return new ResourceDto(
                resourceDomainModel.id(),
                resourceDomainModel.content()
        );
    }
}
