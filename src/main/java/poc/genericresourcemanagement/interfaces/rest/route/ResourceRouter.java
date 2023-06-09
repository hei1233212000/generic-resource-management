package poc.genericresourcemanagement.interfaces.rest.route;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.interfaces.model.AccountDto;
import poc.genericresourcemanagement.interfaces.model.PageableDto;
import poc.genericresourcemanagement.interfaces.model.UserDto;
import poc.genericresourcemanagement.interfaces.rest.handler.ResourceHandler;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Log4j2
public class ResourceRouter {
    @RouterOperations({
            @RouterOperation(
                    path = "/resources/{type}/",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = {RequestMethod.GET},
                    beanClass = ResourceHandler.class,
                    beanMethod = "getResources",
                    operation = @Operation(
                            operationId = "getResources",
                            description = "retrieve resources",
                            parameters = {
                                    @Parameter(
                                            name = "type", in = ParameterIn.PATH,
                                            schema = @Schema(implementation = ResourceType.class)
                                    ),
                                    @Parameter(
                                            name = "size", in = ParameterIn.QUERY,
                                            description = "default size is 50",
                                            schema = @Schema(implementation = Integer.class)
                                    ),
                                    @Parameter(
                                            name = "page", in = ParameterIn.QUERY,
                                            description = "default page is 0",
                                            schema = @Schema(implementation = Integer.class)
                                    ),
                                    @Parameter(
                                            name = "sort", in = ParameterIn.QUERY,
                                            description = "sort the results by different fields with direction (default ascending); e.g. id,createdTime-,updatedTime+",
                                            schema = @Schema(implementation = String.class)
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(oneOf = {UserPageableDto.class, AccountPageableDto.class}))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/resources/{type}/{resourceId}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = {RequestMethod.GET},
                    beanClass = ResourceHandler.class,
                    beanMethod = "getResource",
                    operation = @Operation(
                            operationId = "getResource",
                            description = "retrieve a single resource",
                            parameters = {
                                    @Parameter(
                                            name = "type", in = ParameterIn.PATH,
                                            schema = @Schema(implementation = ResourceType.class)
                                    ),
                                    @Parameter(name = "resourceId", in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(oneOf = {UserDto.class, AccountDto.class}))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Resource not found")
                            }
                    )
            )
    })
    @Bean
    RouterFunction<ServerResponse> resourceRoutes(final ResourceHandler resourceHandler) {
        return RouterFunctions
                .nest(path("/resources"), route()
                        .GET(
                                "/{type}",
                                accept(APPLICATION_JSON),
                                resourceHandler::getResources)
                        .GET(
                                "/{type}/",
                                accept(APPLICATION_JSON),
                                resourceHandler::getResources)
                        .GET(
                                "/{type}/{id}",
                                accept(APPLICATION_JSON),
                                resourceHandler::getResource)
                        .build()
                );
    }

    /**
     * Just for OpenAPI documentation
     */
    public static class UserPageableDto extends PageableDto<UserDto> {}

    /**
     * Just for OpenAPI documentation
     */
    public static class AccountPageableDto extends PageableDto<AccountDto> {}
}
