package poc.genericresourcemanagement.test.cucumber.common;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.DataTable;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import poc.genericresourcemanagement.interfaces.model.PageableDto;

import java.util.Map;
import java.util.function.BiConsumer;

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

    public static void verifyPageableInfo(
            final Response response,
            final DataTable dataTable
    ) {
        final PageableDto<?> pageableDto = response.body().as(new TypeRef<>() {});
        final Map<String, String> expectedData = dataTable.asMap(String.class, String.class);
        final BiConsumer<Number, String> verification = (number, fieldName) -> {
            if(expectedData.containsKey(fieldName)) {
                assertThat(String.valueOf(number))
                        .as("%s is not matched", fieldName)
                        .isEqualTo(expectedData.get(fieldName));
            }
        };
        verification.accept(pageableDto.getPageNumber(), "pageNumber");
        verification.accept(pageableDto.getPageSize(), "pageSize");
        verification.accept(pageableDto.getNumberOfElements(), "numberOfElements");
        verification.accept(pageableDto.getTotalPages(), "totalPages");
        verification.accept(pageableDto.getTotalElements(), "totalElements");
    }
}
