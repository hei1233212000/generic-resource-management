package poc.genericresourcemanagement.interfaces.config;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components()
                .addSchemas(
                        "CreateResourceRequestContent",
                        jsonNodeSchemaWithDifferentDescription("the resource going to be created in JSON format")
                )
                .addSchemas(
                        "ResourceContent",
                        jsonNodeSchemaWithDifferentDescription("This is the actual resource content in JSON format")
                )
        );
    }

    private Schema<?> jsonNodeSchemaWithDifferentDescription(final String description) {
        return schemaWithDifferentDescription(JsonNode.class, description);
    }

    private Schema<?> schemaWithDifferentDescription(final Class<?> className, final String description) {
        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(className).resolveAsRef(false));
        return resolvedSchema.schema.description(description);
    }
}
