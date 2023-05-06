package poc.genericresourcemanagement.interfaces.rest.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import poc.genericresourcemanagement.domain.model.ResourceType;

public interface ApiHandler {
    default ResourceType extractResourceTypeFromPath(final ServerRequest request) {
        return ResourceType.valueOf(extractFromPath(request, "type"));
    }

    default long extractIdFromPath(final ServerRequest request) {
        return extractLongFromPath(request, "id");
    }

    default long extractLongFromPath(final ServerRequest request, final String name) {
        return Long.parseLong(request.pathVariable(name));
    }

    default String extractFromPath(final ServerRequest request, final String name) {
        return request.pathVariable(name);
    }
}
