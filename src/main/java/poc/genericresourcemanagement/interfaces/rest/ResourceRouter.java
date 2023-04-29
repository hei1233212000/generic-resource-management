package poc.genericresourcemanagement.interfaces.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.model.Operation;
import poc.genericresourcemanagement.application.service.resource.ResourceService;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.interfaces.model.CreateResourceRequestDto;
import poc.genericresourcemanagement.interfaces.model.ResourceDto;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Log4j2
public class ResourceRouter {
    @Bean
    RouterFunction<ServerResponse> resourceRoutes(
            final ResourceService resourceService
    ) {
        return RouterFunctions.route()
                .GET("/resources/{type}/{id}", accept(APPLICATION_JSON),
                        request -> getResource(request, resourceService))
                .GET("/resources/{type}/", accept(APPLICATION_JSON),
                        request -> getResources(request, resourceService))
                .GET("/resources/{type}", accept(APPLICATION_JSON),
                        request -> getResources(request, resourceService))
                .POST("/resources/{type}/",
                        contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                        request -> createResource(request, resourceService))
                .POST("/resources/{type}",
                        contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                        request -> createResource(request, resourceService))
                .POST("/resources/{type}/{id}/approve", accept(APPLICATION_JSON),
                        request -> approveOrCancelResource(request, resourceService, Operation.APPROVE))
                .POST("/resources/{type}/{id}/cancel", accept(APPLICATION_JSON),
                        request -> approveOrCancelResource(request, resourceService, Operation.CANCEL))
                .build();
    }

    private Mono<ServerResponse> getResources(
            final ServerRequest request,
            final ResourceService resourceService
    ) {
        return resourceService.findResourceDomainModelsByType(extractResourceType(request))
                .collectList()
                .map(l -> l.stream().map(ResourceRouter::convert2ResourceDto).collect(Collectors.toList()))
                .flatMap(resources -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resources))
                );
    }

    private Mono<ServerResponse> getResource(
            final ServerRequest request,
            final ResourceService resourceService
    ) {
        return resourceService.findResourceDomainModelById(
                        extractResourceType(request),
                        extractResourceRequestId(request)
                )
                .map(ResourceRouter::convert2ResourceDto)
                .flatMap(resource -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resource))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<ServerResponse> createResource(final ServerRequest request, final ResourceService resourceService) {
        return request.bodyToMono(CreateResourceRequestDto.class)
                .map(r -> CreateResourceRequest.builder()
                        .type(extractResourceType(request))
                        .reason(r.reason())
                        .content(r.content())
                        .createdBy("user")
                        .build())
                .flatMap(resourceService::createResource)
                .map(ResourceRouter::convert2ResourceDto)
                .flatMap(resource -> ServerResponse.status(HttpStatusCode.valueOf(201))
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resource))
                );
    }

    private Mono<ServerResponse> approveOrCancelResource(
            final ServerRequest request,
            final ResourceService resourceService,
            final Operation operation
    ) {
        return resourceService.approveOrCancelResource(
                        extractResourceType(request), extractResourceRequestId(request), operation
                )
                .map(ResourceRouter::convert2ResourceDto)
                .flatMap(resource -> ServerResponse.status(HttpStatusCode.valueOf(200))
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resource))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private static ResourceDto convert2ResourceDto(final ResourceDomainModel resourceDomainModel) {
        return ResourceDto.builder()
                .type(resourceDomainModel.type())
                .id(resourceDomainModel.id())
                .content(resourceDomainModel.content())
                .reason(resourceDomainModel.reason())
                .operation(resourceDomainModel.operation())
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
