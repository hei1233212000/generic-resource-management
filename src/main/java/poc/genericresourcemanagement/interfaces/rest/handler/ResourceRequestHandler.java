package poc.genericresourcemanagement.interfaces.rest.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.model.Query;
import poc.genericresourcemanagement.application.model.RequestOperation;
import poc.genericresourcemanagement.application.model.SearchCriteria;
import poc.genericresourcemanagement.application.service.resource.ResourceRequestService;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.interfaces.model.CreateResourceRequestDto;
import poc.genericresourcemanagement.interfaces.model.PageableDto;
import poc.genericresourcemanagement.interfaces.model.ResourceRequestDto;
import poc.genericresourcemanagement.interfaces.rest.util.Queries;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static poc.genericresourcemanagement.application.model.SearchCriteria.SearchCriteriaOperator.eq;

@RequiredArgsConstructor
public class ResourceRequestHandler implements ApiHandler {
    private final ResourceRequestService resourceRequestService;

    public Mono<ServerResponse> getResourceRequests(
            final ServerRequest request
    ) {
        final ResourceType type = extractResourceTypeFromPath(request);
        final SearchCriteria searchType = new SearchCriteria("type", eq, type);
        final Query query = Queries.generateQuery(request, searchType);
        return resourceRequestService.findResourceRequestDomainModelsByType(query)
                .map(p -> {
                    final List<ResourceRequestDto> resourceRequestDtos = p.data().stream()
                                    .map(ResourceRequestHandler::convert2ResourceRequestDto)
                                    .collect(Collectors.toList());
                    return PageableDto.<ResourceRequestDto>builder()
                            .pageNumber(p.pageNumber())
                            .pageSize(p.pageSize())
                            .numberOfElements(p.numberOfElements())
                            .totalPages(p.totalPages())
                            .totalElements(p.totalElements())
                            .data(resourceRequestDtos)
                            .build();
                })
                .flatMap(pageableDto -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(pageableDto))
                );
    }

    public Mono<ServerResponse> getResourceRequest(
            final ServerRequest request
    ) {
        return resourceRequestService.findResourceRequestDomainModelById(
                        extractResourceTypeFromPath(request),
                        extractIdAsLongFromPath(request)
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
                        .type(extractResourceTypeFromPath(request))
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
                        extractResourceTypeFromPath(request), extractIdAsLongFromPath(request), requestOperation
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
}
