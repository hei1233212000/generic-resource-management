package poc.genericresourcemanagement.interfaces.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.service.resource.ResourceService;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.interfaces.model.CreateResourceRequestDto;
import poc.genericresourcemanagement.interfaces.model.ResourceDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

public class ResourceRouter {
    @Bean
    RouterFunction<ServerResponse> resourceRoutes(
            final ResourceService resourceService
    ) {
        return RouterFunctions.route()
                .GET("/resources/{type}/{id}", accept(MediaType.APPLICATION_JSON),
                        request -> getResource(request, resourceService))
                .GET("/resources/{type}/", accept(MediaType.APPLICATION_JSON),
                        request -> getResources(request, resourceService))
                .GET("/resources/{type}", accept(MediaType.APPLICATION_JSON),
                        request -> getResources(request, resourceService))
                .POST("/resources/{type}/",
                        contentType(MediaType.APPLICATION_JSON).and(accept(MediaType.APPLICATION_JSON)),
                        request -> createResource(request, resourceService))
                .POST("/resources/{type}",
                        contentType(MediaType.APPLICATION_JSON).and(accept(MediaType.APPLICATION_JSON)),
                        request -> createResource(request, resourceService))
                .POST("/resources/{type}/{id}/approve", accept(MediaType.APPLICATION_JSON),
                        request -> approveResource(request, resourceService))
                .build();
    }

    private Mono<ServerResponse> getResources(
            final ServerRequest request,
            final ResourceService resourceService
    ) {
        final ResourceDomainModel.ResourceType type = extractResourceType(request);
        final Mono<List<ResourceDto>> resource = resourceService.findResourceDomainModelsByType(type)
                .collectList()
                .map(l -> l.stream().map(ResourceRouter::convert).collect(Collectors.toList()));
        return ServerResponse.ok()
                .body(BodyInserters.fromPublisher(
                        resource,
                        new ParameterizedTypeReference<>() {})
                );
    }

    private Mono<ServerResponse> getResource(
            final ServerRequest request,
            final ResourceService resourceService
    ) {
        final Mono<ResourceDto> resource =
                resourceService.findResourceDomainModelById(
                                extractResourceType(request),
                                extractResourceRequestId(request)
                        )
                        .map(ResourceRouter::convert);
        return ServerResponse.ok()
                .body(BodyInserters.fromPublisher(resource, ResourceDto.class));
    }

    private Mono<ServerResponse> createResource(final ServerRequest request, final ResourceService resourceService) {
        final ResourceDomainModel.ResourceType type = extractResourceType(request);
        final Mono<ResourceDto> resource = request.bodyToMono(CreateResourceRequestDto.class)
                .map(r -> CreateResourceRequest.builder()
                        .type(type)
                        .reason(r.reason())
                        .content(r.content())
                        .createdBy("user")
                        .build())
                .flatMap(resourceService::createResource)
                .map(ResourceRouter::convert);
        return ServerResponse.status(HttpStatusCode.valueOf(201))
                .body(BodyInserters.fromPublisher(resource, ResourceDto.class));
    }

    private Mono<ServerResponse> approveResource(
            final ServerRequest request,
            final ResourceService resourceService
    ) {
        final ResourceDomainModel.ResourceType type = extractResourceType(request);
        final long resourceRequestId = extractResourceRequestId(request);
        final Mono<ResourceDto> resource = resourceService.approveResource(type, resourceRequestId)
                .map(ResourceRouter::convert);
        return ServerResponse.status(HttpStatusCode.valueOf(200))
                .body(BodyInserters.fromPublisher(resource, ResourceDto.class));
    }

    private static ResourceDto convert(final ResourceDomainModel resourceDomainModel) {
        return ResourceDto.builder()
                .type(resourceDomainModel.type())
                .id(resourceDomainModel.id())
                .content(resourceDomainModel.content())
                .reason(resourceDomainModel.reason())
                .status(resourceDomainModel.status())
                .version(resourceDomainModel.version())
                .createdBy(resourceDomainModel.createdBy())
                .createdTime(resourceDomainModel.createdTime())
                .updatedBy(resourceDomainModel.updatedBy())
                .updatedTime(resourceDomainModel.updatedTime())
                .build();
    }

    private static ResourceDomainModel.ResourceType extractResourceType(final ServerRequest request) {
        return ResourceDomainModel.ResourceType.valueOf(request.pathVariable("type"));
    }

    private static long extractResourceRequestId(final ServerRequest request) {
        return Long.parseLong(request.pathVariable("id"));
    }
}
