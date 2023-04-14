package poc.genericresourcemanagement.infrastructure.persistence.model.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class JsonNode2StringConverter implements Converter<JsonNode, String> {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public String convert(final JsonNode jsonNode) {
        return objectMapper.writeValueAsString(jsonNode);
    }
}
