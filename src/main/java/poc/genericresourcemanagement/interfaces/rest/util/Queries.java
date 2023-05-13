package poc.genericresourcemanagement.interfaces.rest.util;

import org.springframework.web.reactive.function.server.ServerRequest;
import poc.genericresourcemanagement.application.model.Order;
import poc.genericresourcemanagement.application.model.Query;
import poc.genericresourcemanagement.application.model.SearchCriteria;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queries {
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String SORT = "sort";
    private static final Set<String> RESERVED_PARAM_NAME = Set.of(PAGE, SIZE, SORT);

    public static Query generateQuery(final ServerRequest serverRequest, SearchCriteria... additionalSearchCriteria) {
        return Query.builder()
                .searchCriteria(generateSearchCriteria(serverRequest, additionalSearchCriteria))
                .page(generatePage(serverRequest))
                .size(generateSize(serverRequest))
                .orders(generateOrders(serverRequest))
                .build();
    }

    private static Set<SearchCriteria> generateSearchCriteria(
            final ServerRequest serverRequest,
            final SearchCriteria... additionalSearchCriteria
    ) {
        final String query = serverRequest.uri().getQuery();
        if(query == null) {
            if (additionalSearchCriteria.length == 0) {
                return Collections.emptySet();
            } else {
                return Arrays.stream(additionalSearchCriteria)
                        .collect(Collectors.toSet());
            }
        }
        final Stream<SearchCriteria> searchCriteriaStream = Arrays.stream(query.split("&"))
                .map(SearchCriteriaUtils::parse)
                .filter(Objects::nonNull)
                .filter(sc -> !RESERVED_PARAM_NAME.contains(sc.getFieldName()));
        return Stream.concat(searchCriteriaStream, Arrays.stream(additionalSearchCriteria))
                .collect(Collectors.toSet());
    }

    private static int generatePage(final ServerRequest serverRequest) {
        final Optional<String> optional = serverRequest.queryParam(PAGE);
        return optional.map(Integer::parseInt)
                .orElse(Query.DEFAULT_PAGE);
    }

    private static int generateSize(final ServerRequest serverRequest) {
        final Optional<String> optional = serverRequest.queryParam(SIZE);
        return optional.map(Integer::parseInt)
                .orElse(Query.DEFAULT_SIZE);
    }

    private static List<Order> generateOrders(final ServerRequest serverRequest) {
        final List<Order> orders = new ArrayList<>();
        if (serverRequest.queryParam(SORT).isPresent()) {
            for(String sort : serverRequest.queryParams().get(SORT)) {
                orders.addAll(Sorts.parse(sort));
            }
        }
        return orders;
    }
}
