package poc.genericresourcemanagement.interfaces.rest.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import poc.genericresourcemanagement.application.model.Order;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static poc.genericresourcemanagement.application.model.Order.Direction.asc;
import static poc.genericresourcemanagement.application.model.Order.Direction.desc;

class SortsTest {
    @DisplayName("should return empty list if the input is not a valid sort info")
    @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
    @ValueSource(strings = {
            "",
            "   ",
            ",",
            "   ,",
            ",  ",
            "   ,  ",
    })
    void shouldReturnEmptyListIfTheInputIsNotAValidSortInfo(final String input) {
        // when
        final List<Order> orders = Sorts.parse(input);

        // then
        assertThat(orders).isEmpty();
    }

    @DisplayName("should able to parse single sort info")
    @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
    @CsvSource({
            "name,asc",
            "name+,asc",
            "name-,desc",
    })
    void shouldAbleToParseSingleSortInfo(final String input, final Order.Direction expectedDirection) {
        // when
        final List<Order> orders = Sorts.parse(input);

        // then
        assertThat(orders).hasSize(1);
        verifyOrder(orders, 0, "name", expectedDirection);
    }

    @Test
    @DisplayName("should able to parse multiple sort info")
    void shouldAbleToParseMultipleSortInfo() {
        // given
        final String input = "name,age+,gender-";

        // when
        final List<Order> orders = Sorts.parse(input);

        // then
        assertThat(orders).hasSize(3);
        verifyOrder(orders, 0, "name", asc);
        verifyOrder(orders, 1, "age", asc);
        verifyOrder(orders, 2, "gender", desc);
    }

    public static void verifyOrder(
            final List<Order> orders, final int orderIndex,
            final String expectedFieldName, final Order.Direction expectedDirection
    ) {
        assertAll(
                () -> assertThat(orders.get(orderIndex).getFieldName()).isEqualTo(expectedFieldName),
                () -> assertThat(orders.get(orderIndex).getDirection()).isEqualTo(expectedDirection)
        );
    }
}