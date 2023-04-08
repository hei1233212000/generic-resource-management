package poc.genericresourcemanagement.infrastructure.persistence.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class String2JsonNodeConverter implements Converter<String, JsonNode> {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public JsonNode convert(final String json) {
        // handle the case that (when use JSON type in H2) from DB returned as a string. e.g. "{\"name\": \"Peter\"}"
        String content = json;
        if(content.startsWith("\"")) {
            content = content.replaceFirst("\"", "");
        }
        if(content.endsWith("\"")) {
            content = content.substring(0, content.length() - 1);
        }
        return objectMapper.readTree(
                content.replaceAll("\\\\\"", "\"")
        );
    }
}
