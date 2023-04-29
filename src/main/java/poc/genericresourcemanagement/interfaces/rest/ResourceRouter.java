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
import poc.genericresourcemanagement.application.service.resource.ResourceRequestService;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.interfaces.model.CreateResourceRequestDto;
import poc.genericresourcemanagement.interfaces.model.ResourceRequestDto;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Log4j2
public class ResourceRouter {
    @Bean
    RouterFunction<ServerResponse> resourceRequestRoutes(
            final ResourceRequestService resourceRequestService
    ) {
        return RouterFunctions.route()
                .GET("/resources/{type}/{id}", accept(APPLICATION_JSON),
                        request -> getResourceRequest(request, resourceRequestService))
                .GET("/resources/{type}/", accept(APPLICATION_JSON),
                        request -> getResourceRequests(request, resourceRequestService))
                .GET("/resources/{type}", accept(APPLICATION_JSON),
                        request -> getResourceRequests(request, resourceRequestService))
                .POST("/resources/{type}/",
                        contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                        request -> createResourceRequest(request, resourceRequestService))
                .POST("/resources/{type}",
                        contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                        request -> createResourceRequest(request, resourceRequestService))
                .POST("/resources/{type}/{id}/approve", accept(APPLICATION_JSON),
                        request -> approveOrCancelResourceRequest(request, resourceRequestService, Operation.APPROVE))
                .POST("/resources/{type}/{id}/cancel", accept(APPLICATION_JSON),
                        request -> approveOrCancelResourceRequest(request, resourceRequestService, Operation.CANCEL))
                .build();
    }

    private Mono<ServerResponse> getResourceRequests(
            final ServerRequest request,
            final ResourceRequestService resourceRequestService
    ) {
        return resourceRequestService.findResourceRequestDomainModelsByType(extractResourceType(request))
                .collectList()
                .map(l -> l.stream().map(ResourceRouter::convert2ResourceRequestDto).collect(Collectors.toList()))
                .flatMap(resourceRequests -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequests))
                );
    }

    private Mono<ServerResponse> getResourceRequest(
            final ServerRequest request,
            final ResourceRequestService resourceRequestService
    ) {
        return resourceRequestService.findResourceRequestDomainModelById(
                        extractResourceType(request),
                        extractResourceRequestId(request)
                )
                .map(ResourceRouter::convert2ResourceRequestDto)
                .flatMap(resourceRequest -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequest))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<ServerResponse> createResourceRequest(final ServerRequest request,
            final ResourceRequestService resourceRequestService) {
        return request.bodyToMono(CreateResourceRequestDto.class)
                .map(r -> CreateResourceRequest.builder()
                        .type(extractResourceType(request))
                        .reason(r.reason())
                        .content(r.content())
                        .createdBy("user")
                        .build())
                .flatMap(resourceRequestService::createResourceRequest)
                .map(ResourceRouter::convert2ResourceRequestDto)
                .flatMap(resourceRequest -> ServerResponse.status(HttpStatusCode.valueOf(201))
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequest))
                );
    }

    private Mono<ServerResponse> approveOrCancelResourceRequest(
            final ServerRequest request,
            final ResourceRequestService resourceRequestService,
            final Operation operation
    ) {
        return resourceRequestService.approveOrCancelResourceRequest(
                        extractResourceType(request), extractResourceRequestId(request), operation
                )
                .map(ResourceRouter::convert2ResourceRequestDto)
                .flatMap(resourceRequest -> ServerResponse.status(HttpStatusCode.valueOf(200))
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequest))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private static ResourceRequestDto convert2ResourceRequestDto(
            final ResourceRequestDomainModel resourceRequestDomainModel
    ) {
        return ResourceRequestDto.builder()
                .type(resourceRequestDomainModel.type())
                .id(resourceRequestDomainModel.id())
                .content(resourceRequestDomainModel.content())
                .reason(resourceRequestDomainModel.reason())
                .operation(resourceRequestDomainModel.operation())
                .status(resourceRequestDomainModel.status())
                .version(resourceRequestDomainModel.version())
                .createdBy(resourceRequestDomainModel.createdBy())
                .createdTime(resourceRequestDomainModel.createdTime())
                .updatedBy(resourceRequestDomainModel.updatedBy())
                .updatedTime(resourceRequestDomainModel.updatedTime())
                .build();
    }

    private static ResourceRequestDomainModel.ResourceType extractResourceType(final ServerRequest request) {
        return ResourceRequestDomainModel.ResourceType.valueOf(request.pathVariable("type"));
    }

    private static long extractResourceRequestId(final ServerRequest request) {
        return Long.parseLong(request.pathVariable("id"));
    }
}
