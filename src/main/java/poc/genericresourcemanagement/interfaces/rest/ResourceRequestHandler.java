package poc.genericresourcemanagement.interfaces.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
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

@RequiredArgsConstructor
public class ResourceRequestHandler {
    private final ResourceRequestService resourceRequestService;

    public Mono<ServerResponse> getResourceRequests(
            final ServerRequest request
    ) {
        return resourceRequestService.findResourceRequestDomainModelsByType(extractResourceType(request))
                .collectList()
                .map(l -> l.stream().map(ResourceRequestHandler::convert2ResourceRequestDto)
                        .collect(Collectors.toList()))
                .flatMap(resourceRequests -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequests))
                );
    }

    public Mono<ServerResponse> getResourceRequest(
            final ServerRequest request
    ) {
        return resourceRequestService.findResourceRequestDomainModelById(
                        extractResourceType(request),
                        extractResourceRequestId(request)
                )
                .map(ResourceRequestHandler::convert2ResourceRequestDto)
                .flatMap(resourceRequest -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequest))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createResourceRequest(final ServerRequest request) {
        return request.bodyToMono(CreateResourceRequestDto.class)
                .map(r -> CreateResourceRequest.builder()
                        .type(extractResourceType(request))
                        .reason(r.reason())
                        .content(r.content())
                        .createdBy("user")
                        .build())
                .flatMap(resourceRequestService::createResourceRequest)
                .map(ResourceRequestHandler::convert2ResourceRequestDto)
                .flatMap(resourceRequest -> ServerResponse.status(HttpStatusCode.valueOf(201))
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequest))
                );
    }

    public Mono<ServerResponse> approveResourceRequest(
            final ServerRequest request
    ) {
        return approveOrCancelResourceRequest(request, RequestOperation.APPROVE);
    }

    public Mono<ServerResponse> cancelResourceRequest(
            final ServerRequest request
    ) {
        return approveOrCancelResourceRequest(request, RequestOperation.CANCEL);
    }

    private Mono<ServerResponse> approveOrCancelResourceRequest(
            final ServerRequest request,
            final RequestOperation requestOperation
    ) {
        return resourceRequestService.approveOrCancelResourceRequest(
                        extractResourceType(request), extractResourceRequestId(request), requestOperation
                )
                .map(ResourceRequestHandler::convert2ResourceRequestDto)
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
