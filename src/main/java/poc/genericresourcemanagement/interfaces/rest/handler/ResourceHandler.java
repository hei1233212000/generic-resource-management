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
import java.util.function.Function;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class ResourceHandler implements ApiHandler {
    public final ResourceService resourceService;
    public final List<ResourceDomainModel2DtoMapper<? extends ResourceDomainModel, ? extends ResourceDto>>
            resourceDomainModel2DtoMappers;

    public Mono<ServerResponse> getResources(final ServerRequest serverRequest) {
        final ResourceType resourceType = extractResourceTypeFromPath(serverRequest);
        return resourceService.findResources(resourceType)
                .collectList()
                .map(domainModel2Dto(resourceType))
                .flatMap(resourceRequests -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(resourceRequests))
                );
    }

    @SuppressWarnings("unchecked")
    private Function<List<? extends ResourceDomainModel>, Stream<ResourceDto>> domainModel2Dto(
            final ResourceType resourceType
    ) {
        return l -> l.stream().map(resourceDomainModel -> {
            final ResourceDomainModel2DtoMapper<ResourceDomainModel, ? extends ResourceDto> resourceDomainModel2DtoMapper =
                    (ResourceDomainModel2DtoMapper<ResourceDomainModel, ? extends ResourceDto>) resourceDomainModel2DtoMappers.stream()
                            .filter(r -> r.isSupported(resourceType))
                            .findFirst()
                            .orElseThrow(
                                    () -> new IllegalStateException(
                                            "cannot find the resource mapper for " + resourceType)
                            );
            return resourceDomainModel2DtoMapper.domainModel2Dto(resourceDomainModel);
        });
    }
}
