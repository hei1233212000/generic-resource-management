package poc.genericresourcemanagement.application.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

@Data
@Builder
public class Query {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 50;

    @Builder.Default
    private Set<SearchCriteria> searchCriteria = emptySet();

    @Builder.Default
    private List<Order> orders = emptyList();

    @Builder.Default
    private int page = DEFAULT_PAGE;

    @Builder.Default
    private int size = DEFAULT_SIZE;
}
