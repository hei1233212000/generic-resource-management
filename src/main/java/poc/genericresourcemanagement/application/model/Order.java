package poc.genericresourcemanagement.application.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    public enum Direction {
        asc, desc
    }

    private final String fieldName;
    private final Direction direction;
}
