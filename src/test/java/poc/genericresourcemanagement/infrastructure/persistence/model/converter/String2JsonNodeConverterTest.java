package poc.genericresourcemanagement.infrastructure.persistence.model.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class String2JsonNodeConverterTest {
    private String2JsonNodeConverter string2JsonNodeConverter;

    @BeforeEach
    void beforeEach() {
        ObjectMapper objectMapper = new ObjectMapper();
        string2JsonNodeConverter = new String2JsonNodeConverter(objectMapper);
    }

    @Test
    @DisplayName("should able to convert a string value to JsonNode")
    void shouldAbleToConvertAStringValueToJsonNode() {
        // given
        verify("{\\\"name\\\": \\\"Peter\\\", \\\"age\\\": 18}");
    }

    @Test
    @DisplayName("should able to convert a JSON string to JsonNode")
    void shouldAbleToConvertAJsonStringToJsonNode() {
        verify("{\"name\": \"Peter\", \"age\": 18}");
    }

    private void verify(final String input) {
        // when
        JsonNode jsonNode = string2JsonNodeConverter.convert(input);

        // then
        assertThat(jsonNode).isNotNull();
        assertThat(jsonNode.get("name").asText()).isEqualTo("Peter");
        assertThat(jsonNode.get("age").asText()).isEqualTo("18");
    }
}