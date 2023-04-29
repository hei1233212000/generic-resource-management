package poc.genericresourcemanagement.test.cucumber.common;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.DataTable;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Verifications {
    public static void verifyJsonNode(final DataTable dataTable, final JsonNode actualResult) {
        final Map<String, String> expectedResultMap = dataTable.asMap();
        expectedResultMap.forEach((fieldName, expectedValue) -> {
            assertThat(actualResult.has(fieldName))
                    .as("the response body does not contain \"%s\"", fieldName)
                    .isTrue();
            assertThat(actualResult.get(fieldName).asText()).isEqualTo(expectedResultMap.get(fieldName));
        });
    }
}
