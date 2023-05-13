package poc.genericresourcemanagement.interfaces.rest.route;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import poc.genericresourcemanagement.interfaces.model.CreateResourceRequestDto;
import poc.genericresourcemanagement.interfaces.model.ErrorResponseDto;
import poc.genericresourcemanagement.interfaces.model.PageableDto;
import poc.genericresourcemanagement.interfaces.model.ResourceRequestDto;
import poc.genericresourcemanagement.interfaces.rest.handler.ResourceRequestHandler;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Log4j2
public class ResourceRequestRouter {
    @RouterOperations({
            @RouterOperation(
                    path = "/resource-requests/{type}/",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = {RequestMethod.GET},
                    beanClass = ResourceRequestHandler.class,
                    beanMethod = "getResourceRequests",
                    operation = @Operation(
                            operationId = "getResourceRequests",
                            description = "retrieve resource requests by type",
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
                                    // now cannot use OpenAPI to provide the doc our the filter. Details: https://github.com/OAI/OpenAPI-Specification/issues/1502
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(implementation = ResourceRequestPageableDto.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/resource-requests/{type}/{resourceRequestId}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = {RequestMethod.GET},
                    beanClass = ResourceRequestHandler.class,
                    beanMethod = "getResourceRequest",
                    operation = @Operation(
                            operationId = "getResourceRequest",
                            description = "retrieve a single resource request",
                            parameters = {
                                    @Parameter(
                                            name = "type", in = ParameterIn.PATH,
                                            schema = @Schema(implementation = ResourceType.class)
                                    ),
                                    @Parameter(name = "resourceRequestId", in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(implementation = ResourceRequestDto.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Resource request not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/resource-requests/{type}/",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    consumes = {MediaType.APPLICATION_JSON_VALUE},
                    method = {RequestMethod.POST},
                    beanClass = ResourceRequestHandler.class,
                    beanMethod = "createResourceRequest",
                    operation = @Operation(
                            operationId = "createResourceRequest",
                            description = "create resource request",
                            parameters = {
                                    @Parameter(
                                            name = "type", in = ParameterIn.PATH,
                                            schema = @Schema(implementation = ResourceType.class)
                                    )
                            },
                            requestBody = @RequestBody(
                                    content = @Content(
                                            schema = @Schema(implementation = CreateResourceRequestDto.class),
                                            examples = {
                                                        @ExampleObject(
                                                            name = "Create User",
                                                            value = """
                                                                    {
                                                                      "content": {
                                                                        "name": "David Wong",
                                                                        "age": 40
                                                                      },
                                                                      "reason": "create David"
                                                                    }
                                                                    """
                                                    ),
                                                    @ExampleObject(
                                                            name = "Create Account",
                                                            value = """
                                                                    {
                                                                      "content": {
                                                                        "id": "59e5ec44-799a-44ad-b202-27a01a1b660f",
                                                                        "holder": "Peter Chan",
                                                                        "amount": "1000000"
                                                                      },
                                                                      "reason": "create new account"
                                                                    }
                                                                    """
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201", description = "successful operation",
                                            content = @Content(schema = @Schema(implementation = ResourceRequestDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400", description = "validation error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/resource-requests/{type}/{resourceRequestId}/approve",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    consumes = {MediaType.APPLICATION_JSON_VALUE},
                    method = {RequestMethod.POST},
                    beanClass = ResourceRequestHandler.class,
                    beanMethod = "approveResourceRequest",
                    operation = @Operation(
                            operationId = "approveResourceRequest",
                            description = "approve a resource request",
                            parameters = {
                                    @Parameter(
                                            name = "type", in = ParameterIn.PATH,
                                            schema = @Schema(implementation = ResourceType.class)
                                    ),
                                    @Parameter(name = "resourceRequestId", in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(implementation = ResourceRequestDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400", description = "validation error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Resource request not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/resource-requests/{type}/{resourceRequestId}/cancel",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    consumes = {MediaType.APPLICATION_JSON_VALUE},
                    method = {RequestMethod.POST},
                    beanClass = ResourceRequestHandler.class,
                    beanMethod = "cancelResourceRequest",
                    operation = @Operation(
                            operationId = "cancelResourceRequest",
                            description = "cancel a resource request",
                            parameters = {
                                    @Parameter(
                                            name = "type", in = ParameterIn.PATH,
                                            schema = @Schema(implementation = ResourceType.class)
                                    ),
                                    @Parameter(name = "resourceRequestId", in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(implementation = ResourceRequestDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400", description = "validation error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Resource request not found")
                            }
                    )
            )
    })
    @Bean
    RouterFunction<ServerResponse> resourceRequestRoutes(
            final ResourceRequestHandler resourceRequestHandler
    ) {
        return RouterFunctions
                .nest(path("/resource-requests"), route()
                        .GET(
                                "/{type}/",
                                accept(APPLICATION_JSON),
                                resourceRequestHandler::getResourceRequests)
                        .GET(
                                "/{type}",
                                accept(APPLICATION_JSON),
                                resourceRequestHandler::getResourceRequests)
                        .GET(
                                "/{type}/{id}",
                                accept(APPLICATION_JSON),
                                resourceRequestHandler::getResourceRequest)

                        .POST(
                                "/{type}/",
                                contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                                resourceRequestHandler::createResourceRequest
                        )
                        .POST(
                                "/{type}",
                                contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)),
                                resourceRequestHandler::createResourceRequest
                        )
                        .POST(
                                "/{type}/{id}/approve",
                                accept(APPLICATION_JSON),
                                resourceRequestHandler::approveResourceRequest
                        )
                        .POST(
                                "/{type}/{id}/cancel",
                                accept(APPLICATION_JSON),
                                resourceRequestHandler::cancelResourceRequest
                        )
                        .build()
                );
    }

    /**
     * Just for OpenAPI documentation
     */
    public static class ResourceRequestPageableDto extends PageableDto<ResourceRequestDto> {}
}
