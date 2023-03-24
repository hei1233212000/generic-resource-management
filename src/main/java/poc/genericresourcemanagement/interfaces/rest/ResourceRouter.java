package poc.genericresourcemanagement.interfaces.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import poc.genericresourcemanagement.application.service.ResourceService;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.interfaces.model.ResourceDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

public class ResourceRouter {
    @Bean
    RouterFunction<ServerResponse> getResourcesRoute(final ResourceService resourceService) {
        return RouterFunctions.route(GET("/resources/{type}/").or(GET("/resources/{type}")),
                request -> {
                    Mono<List<ResourceDto>> resource =
                            resourceService.findResourceDomainModelsByType(
                                            ResourceDomainModel.ResourceType.valueOf(request.pathVariable("type"))
                                    )
                                    .collectList()
                                    .map(l -> l.stream().map(this::convert).collect(Collectors.toList()));
                    return ServerResponse.ok()
                            .body(BodyInserters.fromPublisher(
                                    resource,
                                    new ParameterizedTypeReference<>() {})
                            );
                }
        );
    }

    @Bean
    RouterFunction<ServerResponse> getResourceRoute(final ResourceService resourceService) {
        return RouterFunctions.route(GET("/resources/{type}/{id}"),
                request -> {
                    Mono<ResourceDto> resource =
                            resourceService.findResourceDomainModelById(
                                            ResourceDomainModel.ResourceType.valueOf(request.pathVariable("type")),
                                            request.pathVariable("id")
                                    )
                                    .map(this::convert);
                    return ServerResponse.ok()
                            .body(BodyInserters.fromPublisher(resource, ResourceDto.class));
                }
        );
    }

    private ResourceDto convert(final ResourceDomainModel resourceDomainModel) {
        return ResourceDto.builder()
                .type(resourceDomainModel.type())
                .id(resourceDomainModel.id())
                .content(resourceDomainModel.content())
                .status(resourceDomainModel.status())
                .version(resourceDomainModel.version())
                .createdBy(resourceDomainModel.createdBy())
                .createdTime(resourceDomainModel.createdTime())
                .updatedBy(resourceDomainModel.updatedBy())
                .updatedTime(resourceDomainModel.updatedTime())
                .build();
    }
}
