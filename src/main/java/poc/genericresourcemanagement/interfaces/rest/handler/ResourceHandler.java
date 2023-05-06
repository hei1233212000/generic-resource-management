package poc.genericresourcemanagement.interfaces.rest.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import poc.genericresourcemanagement.application.service.resource.ResourceService;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.interfaces.model.ResourceDto;
import poc.genericresourcemanagement.interfaces.rest.mapper.ResourceDomainModel2DtoMapper;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class ResourceHandler implements ApiHandler {
    public final ResourceService resourceService;
    public final List<ResourceDomainModel2DtoMapper<? extends ResourceDomainModel, ? extends ResourceDto>>
            resourceDomainModel2DtoMappers;

    public Mono<ServerResponse> getResources(final ServerRequest serverRequest) {
        final ResourceType resourceType = extractResourceTypeFromPath(serverRequest);
        final ResourceDomainModel2DtoMapper<ResourceDomainModel, ? extends ResourceDto>
                resourceDomainModel2DtoMapper = findResourceDomainModel2DtoMapper(resourceType);
        return resourceService.findResources(resourceType)
                .collectList()
                .map(l -> l.stream().map(resourceDomainModel2DtoMapper::domainModel2Dto))
                .flatMap(resourceRequests -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequests))
                );
    }

    public Mono<ServerResponse> getResource(final ServerRequest serverRequest) {
        final ResourceType resourceType = extractResourceTypeFromPath(serverRequest);
        final String id = extractIdFromPath(serverRequest);
        final ResourceDomainModel2DtoMapper<ResourceDomainModel, ? extends ResourceDto>
                resourceDomainModel2DtoMapper = findResourceDomainModel2DtoMapper(resourceType);
        return resourceService.findResource(resourceType, id)
                .map(resourceDomainModel2DtoMapper::domainModel2Dto)
                .flatMap(resourceRequests -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequests))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @SuppressWarnings("unchecked")
    private ResourceDomainModel2DtoMapper<ResourceDomainModel, ? extends ResourceDto> findResourceDomainModel2DtoMapper(
            final ResourceType resourceType
    ) {
        return (ResourceDomainModel2DtoMapper<ResourceDomainModel, ? extends ResourceDto>) resourceDomainModel2DtoMappers.stream()
                .filter(r -> r.isSupported(resourceType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(
                                "cannot find the resource mapper for " + resourceType)
                );
    }
}
