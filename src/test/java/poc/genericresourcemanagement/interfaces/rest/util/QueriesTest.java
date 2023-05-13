package poc.genericresourcemanagement.interfaces.rest.util;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import poc.genericresourcemanagement.application.model.Order;
import poc.genericresourcemanagement.application.model.Query;
import poc.genericresourcemanagement.application.model.SearchCriteria;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static poc.genericresourcemanagement.application.model.Order.Direction.asc;
import static poc.genericresourcemanagement.application.model.Order.Direction.desc;
import static poc.genericresourcemanagement.application.model.Query.DEFAULT_PAGE;
import static poc.genericresourcemanagement.application.model.Query.DEFAULT_SIZE;
import static poc.genericresourcemanagement.application.model.SearchCriteria.SearchCriteriaOperator.*;

@Log4j2
class QueriesTest {
    @Test
    @DisplayName("should have default values even no query parameters")
    void shouldHaveDefaultValuesEvenNoQueryParameters() {
        // given
        final ServerRequest serverRequest = mockServerRequest();

        // when
        final Query query = generateQuery(serverRequest);

        // then
        assertAll(
                () -> assertThat(query.getSearchCriteria()).isEmpty(),
                () -> assertThat(query.getOrders()).isEmpty(),
                () -> assertThat(query.getPage()).isEqualTo(DEFAULT_PAGE),
                () -> assertThat(query.getSize()).isEqualTo(DEFAULT_SIZE)
        );
    }

    @Test
    @DisplayName("should able to parse the complex query")
    void shouldAbleToParseTheComplexQuery() {
        // given
        final String queryParameters = "eq(name,Peter)"
                + "&like(reason,XXX-*)"
                + "&in(id,(1,2,3))"
                + "&size=50&page=1"
                + "&sort=name,age-"
                + "&sort=gender+";
        final ServerRequest serverRequest = mockServerRequest(queryParameters);

        // when
        final Query query = generateQuery(serverRequest);
        final Set<SearchCriteria> searchCriteria = query.getSearchCriteria();

        //then
        assertThat(searchCriteria).hasSize(3);
        verifySearchCriteria(searchCriteria, "name", eq, "Peter");
        verifySearchCriteria(searchCriteria, "reason", like, "XXX-*");
        verifySearchCriteria(searchCriteria, "id", in, Set.of("1", "2", "3"));

        assertAll(
                () -> assertThat(query.getPage()).isEqualTo(1),
                () -> assertThat(query.getSize()).isEqualTo(50)
        );

        final List<Order> orders = query.getOrders();
        assertThat(orders).hasSize(3);
        SortsTest.verifyOrder(orders, 0, "name", asc);
        SortsTest.verifyOrder(orders, 1, "age", desc);
        SortsTest.verifyOrder(orders, 2, "gender", asc);
    }

    @SneakyThrows
    @Test
    @DisplayName("should able to parse the search criteria from URL")
    void shouldAbleToParseTheSearchCriteriaFromUrl() {
        // given
        final ServerRequest serverRequest = mockServerRequest("eq(name,Peter)");

        // when
        final Query query = generateQuery(serverRequest);
        final Set<SearchCriteria> searchCriteria = query.getSearchCriteria();

        //then
        assertThat(searchCriteria).hasSize(1);
        verifySearchCriteria(searchCriteria, "name", eq, "Peter");
    }

    @SneakyThrows
    @Test
    @DisplayName("should able to parse multiple search criteria from URL")
    void shouldAbleToParseMultipleSearchCriteriaFromUrl() {
        // given
        final String queryParameters = "reason=This%20is%20a%20test%2C%20Peter"
                + "&eq(name,Peter)"
                + "&ne(name,David)"
                + "&gt(age,20)"
                + "&ge(age,15)"
                + "&lt(age,100)"
                + "&le(age,101)"
                + "&like(reason,XXX-*)"
                + "&ilike(reason,YYY-*)"
                + "&in(id,(1,2,3))"
                + "&out(name,(Peter,Paul))";
        final ServerRequest serverRequest = mockServerRequest(queryParameters);

        // when
        final Query query = generateQuery(serverRequest);
        final Set<SearchCriteria> searchCriteria = query.getSearchCriteria();

        //then
        assertThat(searchCriteria).hasSize(11);
        verifySearchCriteria(searchCriteria, "reason", eq, "This is a test, Peter");
        verifySearchCriteria(searchCriteria, "name", eq, "Peter");
        verifySearchCriteria(searchCriteria, "name", ne, "David");
        verifySearchCriteria(searchCriteria, "age", gt, "20");
        verifySearchCriteria(searchCriteria, "age", ge, "15");
        verifySearchCriteria(searchCriteria, "age", lt, "100");
        verifySearchCriteria(searchCriteria, "age", le, "101");
        verifySearchCriteria(searchCriteria, "reason", like, "XXX-*");
        verifySearchCriteria(searchCriteria, "reason", ilike, "YYY-*");
        verifySearchCriteria(searchCriteria, "id", in, Set.of("1", "2", "3"));
        verifySearchCriteria(searchCriteria, "name", out, Set.of("Peter", "Paul"));
    }

    @Test
    @DisplayName("should able to parse the page")
    void shouldAbleToParseThePage() {
        // given
        final ServerRequest serverRequest = mockServerRequest("page=0");

        // when
        final Query query = generateQuery(serverRequest);

        // then
        assertAll(
                () -> assertThat(query.getPage()).isEqualTo(0),
                () -> assertThat(query.getSize()).isEqualTo(DEFAULT_SIZE)
        );
    }

    @Test
    @DisplayName("should able to parse the size")
    void shouldAbleToParseTheSize() {
        // given
        final ServerRequest serverRequest = mockServerRequest("size=100");

        // when
        final Query query = generateQuery(serverRequest);

        // then
        assertAll(
                () -> assertThat(query.getPage()).isEqualTo(DEFAULT_PAGE),
                () -> assertThat(query.getSize()).isEqualTo(100)
        );
    }

    @Test
    @DisplayName("should able to parse the page and size")
    void shouldAbleToParseThePageAndSize() {
        // given
        final ServerRequest serverRequest = mockServerRequest("page=0&size=100");

        // when
        final Query query = generateQuery(serverRequest);

        // then
        assertAll(
                () -> assertThat(query.getPage()).isEqualTo(0),
                () -> assertThat(query.getSize()).isEqualTo(100)
        );
    }

    @Test
    @DisplayName("should able to add single additional search criteria to empty criteria")
    void shouldAbleToAddSingleAdditionalSearchCriteriaToEmptyCriteria() {
        // given
        final ServerRequest serverRequest = mockServerRequest();
        final SearchCriteria additionalSearchCriteria = new SearchCriteria("name", eq, "Peter");

        // when
        final Query query = generateQuery(serverRequest, additionalSearchCriteria);
        final Set<SearchCriteria> searchCriteria = query.getSearchCriteria();

        //then
        assertThat(searchCriteria).hasSize(1);
        verifySearchCriteria(searchCriteria, "name", eq, "Peter");
    }

    @Test
    @DisplayName("should able to add multiple additional search criteria to empty criteria")
    void shouldAbleToAddMultipleAdditionalSearchCriteriaToEmptyCriteria() {
        // given
        final ServerRequest serverRequest = mockServerRequest();
        final SearchCriteria additionalSearchCriteria_1 = new SearchCriteria("name", eq, "Peter");
        final SearchCriteria additionalSearchCriteria_2 = new SearchCriteria("age", lt, "11");

        // when
        final Query query = generateQuery(serverRequest, additionalSearchCriteria_1, additionalSearchCriteria_2);
        final Set<SearchCriteria> searchCriteria = query.getSearchCriteria();

        //then
        assertThat(searchCriteria).hasSize(2);
        verifySearchCriteria(searchCriteria, "name", eq, "Peter");
        verifySearchCriteria(searchCriteria, "age", lt, "11");
    }

    @Test
    @DisplayName("should able to add additional search criteria")
    void shouldAbleToAddAdditionalSearchCriteria() {
        // given
        final ServerRequest serverRequest = mockServerRequest("like(reason,XXX-*)");
        final SearchCriteria additionalSearchCriteria_1 = new SearchCriteria("name", eq, "Peter");
        final SearchCriteria additionalSearchCriteria_2 = new SearchCriteria("age", lt, "11");

        // when
        final Query query = generateQuery(serverRequest, additionalSearchCriteria_1, additionalSearchCriteria_2);
        final Set<SearchCriteria> searchCriteria = query.getSearchCriteria();

        //then
        assertThat(searchCriteria).hasSize(3);
        verifySearchCriteria(searchCriteria, "name", eq, "Peter");
        verifySearchCriteria(searchCriteria, "age", lt, "11");
        verifySearchCriteria(searchCriteria, "reason", like, "XXX-*");
    }

    private static Query generateQuery(
            final ServerRequest serverRequest,
            final SearchCriteria... additionalSearchCriteria
    ) {
        final Query query = Queries.generateQuery(serverRequest, additionalSearchCriteria);
        log.info("query: {}", query);
        return query;
    }

    private void verifySearchCriteria(
            final Set<SearchCriteria> searchCriteria,
            final String expectedFieldName,
            final SearchCriteria.SearchCriteriaOperator expectedOperator,
            final String expectedValue
    ) {
        final SearchCriteria criteria = findSearchCriteria(searchCriteria, expectedFieldName, expectedOperator);
        assertAll(
                () -> assertThat(criteria.getFieldName()).isEqualTo(expectedFieldName),
                () -> assertThat(criteria.getOperator()).isEqualTo(expectedOperator),
                () -> assertThat(criteria.getValue()).isEqualTo(expectedValue)
        );
    }

    @SuppressWarnings("unchecked")
    private void verifySearchCriteria(
            final Set<SearchCriteria> searchCriteria,
            final String expectedFieldName,
            final SearchCriteria.SearchCriteriaOperator expectedOperator,
            final Set<String> expectedValue
    ) {
        final SearchCriteria criteria = findSearchCriteria(searchCriteria, expectedFieldName, expectedOperator);
        assertAll(
                () -> assertThat(criteria.getFieldName()).isEqualTo(expectedFieldName),
                () -> assertThat(criteria.getOperator()).isEqualTo(expectedOperator),
                () -> assertThat(criteria.getValue()).isInstanceOf(Set.class),
                () -> assertThat((Set<String>) criteria.getValue())
                        .containsExactlyInAnyOrderElementsOf(expectedValue)
        );
    }

    private SearchCriteria findSearchCriteria(final Set<SearchCriteria> searchCriteria,
            final String expectedFieldName, final SearchCriteria.SearchCriteriaOperator expectedOperator) {
        return searchCriteria.stream()
                .filter(sc -> sc.getFieldName().equals(expectedFieldName) && sc.getOperator() == expectedOperator)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                                String.format("fail to find search criteria[fieldName: %s, operation: %s]",
                                        expectedFieldName, expectedOperator)
                        )
                );
    }

    private static ServerRequest mockServerRequest() {
        return mockServerRequest(null);
    }

    @SneakyThrows
    private static ServerRequest mockServerRequest(final String queryParameters) {
        final ServerRequest serverRequest = mock(ServerRequest.class);
        final URI uri = new URI("http://localhost" + (queryParameters == null ? "" : "?" + queryParameters));
        given(serverRequest.uri())
                .willReturn(uri);

        if(queryParameters != null) {
            final Map<String, String> queryParamMap = new HashMap<>();
            final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            Arrays.stream(queryParameters.split("&"))
                    .filter(qp -> qp.contains("="))
                    .forEach(qp -> {
                        final String[] tokens = qp.split("=");
                        final String parameterName = tokens[0];
                        final String value = tokens[1];
                        queryParamMap.computeIfAbsent(parameterName, key -> value);
                        queryParams.add(parameterName, value);
                    });
            given(serverRequest.queryParam(anyString()))
                    .willAnswer(invocation -> {
                        final String fieldName = invocation.getArgument(0);
                        if(queryParamMap.containsKey(fieldName)) {
                            return Optional.of(queryParamMap.get(fieldName));
                        } else {
                            return Optional.empty();
                        }
                    });
            given(serverRequest.queryParams())
                    .willReturn(queryParams);
        }
        return serverRequest;
    }
}