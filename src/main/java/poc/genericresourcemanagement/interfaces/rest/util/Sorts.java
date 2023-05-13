package poc.genericresourcemanagement.interfaces.rest.util;

import poc.genericresourcemanagement.application.model.Order;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sorts {
    public static List<Order> parse(final String parameter) {
        return Arrays.stream(parameter.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(value -> {
                    final String fieldName;
                    final Order.Direction direction;
                    if(value.endsWith("+")) {
                        fieldName = removeTheSign(value);
                        direction = Order.Direction.asc;
                    } else if(value.endsWith("-")) {
                        fieldName = removeTheSign(value);
                        direction = Order.Direction.desc;
                    } else {
                        fieldName = value;
                        direction = Order.Direction.asc;
                    }
                    return Order.builder()
                            .fieldName(fieldName)
                            .direction(direction)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static String removeTheSign(final String value) {
        return value.substring(0, value.length() - 1);
    }
}
