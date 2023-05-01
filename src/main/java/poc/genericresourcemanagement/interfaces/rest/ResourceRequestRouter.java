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
import poc.genericresourcemanagement.application.model.RequestOperation;
import poc.genericresourcemanagement.application.service.resource.ResourceRequestService;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.interfaces.model.CreateResourceRequestDto;
import poc.genericresourcemanagement.interfaces.model.ResourceRequestDto;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Log4j2
public class ResourceRequestRouter {
    @Bean
    RouterFunction<ServerResponse> resourceRequestRoutes(
            final ResourceRequestService resourceRequestService
    ) {
        return RouterFunctions.route()
                .GET("/resource-requests/{type}/{id}", accept(APPLICATION_JSON),
                        request -> getResourceRequest(request, resourceRequestService))
                .GET("/resource-requests/{type}/", accept(APPLICATION_JSON),
                        request -> getResourceRequests(request, resourceRequestService))
                .GET("/resource-requests/{type}", accept(APPLICATION_JSON),
                        request -> getResourceRequests(request, resourceRequestService))
                .POST("/resource-requests/{type}/",
                        contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                        request -> createResourceRequest(request, resourceRequestService))
                .POST("/resource-requests/{type}",
                        contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                        request -> createResourceRequest(request, resourceRequestService))
                .POST("/resource-requests/{type}/{id}/approve", accept(APPLICATION_JSON),
                        request -> approveOrCancelResourceRequest(request, resourceRequestService, RequestOperation.APPROVE))
                .POST("/resource-requests/{type}/{id}/cancel", accept(APPLICATION_JSON),
                        request -> approveOrCancelResourceRequest(request, resourceRequestService, RequestOperation.CANCEL))
                .build();
    }

    private Mono<ServerResponse> getResourceRequests(
            final ServerRequest request,
            final ResourceRequestService resourceRequestService
    ) {
        return resourceRequestService.findResourceRequestDomainModelsByType(extractResourceType(request))
                .collectList()
                .map(l -> l.stream().map(ResourceRequestRouter::convert2ResourceRequestDto)
                        .collect(Collectors.toList()))
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
                .map(ResourceRequestRouter::convert2ResourceRequestDto)
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
                .map(ResourceRequestRouter::convert2ResourceRequestDto)
                .flatMap(resourceRequest -> ServerResponse.status(HttpStatusCode.valueOf(201))
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequest))
                );
    }

    private Mono<ServerResponse> approveOrCancelResourceRequest(
            final ServerRequest request,
            final ResourceRequestService resourceRequestService,
            final RequestOperation requestOperation
    ) {
        return resourceRequestService.approveOrCancelResourceRequest(
                        extractResourceType(request), extractResourceRequestId(request), requestOperation
                )
                .map(ResourceRequestRouter::convert2ResourceRequestDto)
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

    private static ResourceType extractResourceType(final ServerRequest request) {
        return ResourceType.valueOf(request.pathVariable("type"));
    }

    private static long extractResourceRequestId(final ServerRequest request) {
        return Long.parseLong(request.pathVariable("id"));
    }
}
