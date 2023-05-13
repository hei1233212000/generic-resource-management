package poc.genericresourcemanagement.application.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import poc.genericresourcemanagement.application.model.Order;
import poc.genericresourcemanagement.application.model.Query;
import poc.genericresourcemanagement.application.model.SearchCriteria;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.data.relational.core.query.CriteriaDefinition.Comparator.*;
import static poc.genericresourcemanagement.application.model.SearchCriteria.SearchCriteriaOperator.*;

@Log4j2
class SpringQueriesTest {

    @Nested
    class ConvertSingleSearchCriteria {
        @Test
        @DisplayName("should able to convert search criteria for equal search")
        void shouldAbleToConvertSearchCriteriaForEqualSearch() {
            final SearchCriteria searchCriteria = new SearchCriteria("name", eq, "Peter");
            testConvertSingleSearchCriteria(searchCriteria, EQ, true);
        }

        @Test
        @DisplayName("should able to convert search criteria for not equal search")
        void shouldAbleToConvertSearchCriteriaForNotEqualSearch() {
            final SearchCriteria searchCriteria = new SearchCriteria("name", ne, "Peter");
            testConvertSingleSearchCriteria(searchCriteria, NEQ, true);
        }

        @Test
        @DisplayName("should able to convert search criteria for greater than search")
        void shouldAbleToConvertSearchCriteriaForGreaterThanSearch() {
            final SearchCriteria searchCriteria = new SearchCriteria("age", gt, BigDecimal.TEN);
            testConvertSingleSearchCriteria(searchCriteria, GT);
        }

        @Test
        @DisplayName("should able to convert search criteria for greater than or equal search")
        void shouldAbleToConvertSearchCriteriaForGreaterThanOrEqualSearch() {
            final SearchCriteria searchCriteria = new SearchCriteria("age", ge, BigDecimal.TEN);
            testConvertSingleSearchCriteria(searchCriteria, GTE);
        }

        @Test
        @DisplayName("should able to convert search criteria for less than search")
        void shouldAbleToConvertSearchCriteriaForLessThanSearch() {
            final SearchCriteria searchCriteria = new SearchCriteria("age", lt, BigDecimal.TEN);
            testConvertSingleSearchCriteria(searchCriteria, LT);
        }

        @Test
        @DisplayName("should able to convert search criteria for less than or equal to")
        void shouldAbleToConvertSearchCriteriaForLessThanOrEqualTo() {
            final SearchCriteria searchCriteria = new SearchCriteria("age", le, BigDecimal.TEN);
            testConvertSingleSearchCriteria(searchCriteria, LTE);
        }

        @Test
        @DisplayName("should able to convert search criteria for like search")
        void shouldAbleToConvertSearchCriteriaForLikeSearch() {
            final SearchCriteria searchCriteria = new SearchCriteria("reason", like, "*This is a Test*");
            testConvertSingleSearchCriteria(searchCriteria, LIKE, true, "%This is a Test%");
        }

        @Test
        @DisplayName("should able to convert search criteria for not like search")
        void shouldAbleToConvertSearchCriteriaForNotLikeSearch() {
            final SearchCriteria searchCriteria = new SearchCriteria("reason", ilike, "*This is a Test*");
            testConvertSingleSearchCriteria(searchCriteria, NOT_LIKE, true, "%This is a Test%");
        }

        @Test
        @DisplayName("should able to convert search criteria for contain search for String values")
        void shouldAbleToConvertSearchCriteriaForContainSearchForStringValues() {
            final SearchCriteria searchCriteria = new SearchCriteria("name", in, Set.of("Peter", "May"));
            testConvertSingleSearchCriteria(searchCriteria, IN);
        }

        @Test
        @DisplayName("should able to convert search criteria for contain search for Number values")
        void shouldAbleToConvertSearchCriteriaForContainSearchForNumberValues() {
            final SearchCriteria searchCriteria = new SearchCriteria("name", in, Set.of(1, 2, 3));
            testConvertSingleSearchCriteria(searchCriteria, IN);
        }

        @Test
        @DisplayName("should able to convert search criteria for not contain search for String values")
        void shouldAbleToConvertSearchCriteriaForNotContainSearchForStringValues() {
            final SearchCriteria searchCriteria = new SearchCriteria("name", out, Set.of("Peter", "May"));
            testConvertSingleSearchCriteria(searchCriteria, NOT_IN);
        }

        @Test
        @DisplayName("should able to convert search criteria for not contain search for Number values")
        void shouldAbleToConvertSearchCriteriaForNotContainSearchForNumberValues() {
            final SearchCriteria searchCriteria = new SearchCriteria("id", out, Set.of(1, 2, 3));
            testConvertSingleSearchCriteria(searchCriteria, NOT_IN);
        }

        @DisplayName("should ignore unknown searching criteria")
        @ParameterizedTest(name = "{index} => when the operation is \"{0}\"")
        @EnumSource(value = SearchCriteria.SearchCriteriaOperator.class)
        void shouldIgnoreUnknownSearchingCriteria(final SearchCriteria.SearchCriteriaOperator operator) {
            // given
            final Object value = operator == in || operator == out ? List.of("1") : "1";
            final Set<SearchCriteria> searchCriteria = Set.of(
                    new SearchCriteria("name", eq, "Peter"),
                    new SearchCriteria("fake", operator, value )
            );
            final Query query = Query.builder()
                    .searchCriteria(searchCriteria)
                    .build();
            final Set<String> searchableFieldNames = Set.of("id", "name");

            // when
            final org.springframework.data.relational.core.query.Query springQuery =
                    SpringQueries.convert(query, searchableFieldNames, emptySet());
            log.info("springQuery: {}", springQuery);

            // when
            assertThat(springQuery.getCriteria().orElseThrow().toString())
                    .isEqualTo("(name = 'Peter')");
        }

        private void testConvertSingleSearchCriteria(
                final SearchCriteria searchCriteria,
                final CriteriaDefinition.Comparator expectedComparator
        ) {
            testConvertSingleSearchCriteria(searchCriteria, expectedComparator, false);
        }

        private void testConvertSingleSearchCriteria(
                final SearchCriteria searchCriteria,
                final CriteriaDefinition.Comparator expectedComparator,
                final boolean expectedIgnoreCase
        ) {
            testConvertSingleSearchCriteria(
                    searchCriteria, expectedComparator, expectedIgnoreCase, searchCriteria.getValue()
            );
        }

        private void testConvertSingleSearchCriteria(
                final SearchCriteria searchCriteria,
                final CriteriaDefinition.Comparator expectedComparator,
                final boolean expectedIgnoreCase,
                final Object expectedValue
        ) {
            // when
            final CriteriaDefinition criteriaDefinition = SpringQueries.convert(searchCriteria);
            log.info("criteriaDefinition: {}", criteriaDefinition);

            // then
            verifySingleCriteriaDefinition(searchCriteria, criteriaDefinition, expectedComparator, expectedIgnoreCase,
                    expectedValue);
        }
    }

    @Nested
    class ConvertMultipleSearchCriteria {
        @Test
        @DisplayName("should able to convert multiple search criteria")
        void shouldAbleToConvertMultipleSearchCriteria() {
            // given
            final List<SearchCriteria> searchCriteria = List.of(
                    new SearchCriteria("name", eq, "Peter"),
                    new SearchCriteria("age", lt, 10),
                    new SearchCriteria("reason", like, "*bug*"),
                    new SearchCriteria("id", in, List.of(1, 2))
            );
            final Set<String> searchableFieldNames = Set.of("id", "name", "age", "reason");

            // when
            final CriteriaDefinition criteriaDefinition = SpringQueries.convert(searchCriteria, searchableFieldNames);
            log.info("criteriaDefinition: {}", criteriaDefinition);

            // then
            assertThat(criteriaDefinition.toString())
                    .isEqualTo("(name = 'Peter') AND (age < 10) AND (reason LIKE '%bug%') AND (id IN (1, 2))");
        }
    }

    @Nested
    class ConvertOrder {
        @DisplayName("should able to covert single order")
        @ParameterizedTest(name = "{index} => when the input direction is \"{0}\", then it should return \"{1}\"")
        @CsvSource({
                "asc,ASC",
                "desc,DESC",
        })
        void shouldAbleToCovertSingleOrder(
                final Order.Direction direction,
                final Sort.Direction expectedDirection
        ) {
            // given
            final Order order = Order.builder()
                    .fieldName("name")
                    .direction(direction)
                    .build();

            // then
            final Sort.Order springOrder = SpringQueries.convert(order);
            log.info("springOrder: {}", springOrder);

            // then
            assertAll(
                    () -> assertThat(springOrder.getProperty()).isEqualTo(order.getFieldName()),
                    () -> assertThat(springOrder.getDirection()).isEqualTo(expectedDirection)
            );
        }

        @Test
        @DisplayName("should able to convert multiple orders")
        void shouldAbleToConvertMultipleOrders() {
            // given
            final List<Order> orders = List.of(
                    Order.builder()
                            .fieldName("name")
                            .direction(Order.Direction.asc)
                            .build(),
                    Order.builder()
                            .fieldName("age")
                            .direction(Order.Direction.desc)
                            .build(),
                    Order.builder()
                            .fieldName("id")
                            .direction(Order.Direction.asc)
                            .build()
            );
            final Set<String> sortableFieldNames = Set.of("id", "name", "age");

            // then
            final Sort springSort = SpringQueries.convert(orders, sortableFieldNames);
            log.info("springSort: {}", springSort);

            // then
            assertThat(springSort.toString()).isEqualTo("name: ASC,age: DESC,id: ASC");
        }

        @DisplayName("should ignore unknown sort fields")
        @ParameterizedTest(name = "{index} => when the input direction is \"{0}\"")
        @CsvSource({
                "asc,ASC",
                "desc,DESC",
        })
        void shouldIgnoreUnknownSortFields(final Order.Direction direction) {
            // given
            final List<Order> orders = List.of(
                    Order.builder()
                            .fieldName("fake")
                            .direction(direction)
                            .build(),
                    Order.builder()
                            .fieldName("id")
                            .direction(Order.Direction.asc)
                            .build()
            );
            final Set<String> sortableFieldNames = Set.of("id");

            // then
            final Sort springSort = SpringQueries.convert(orders, sortableFieldNames);
            log.info("springSort: {}", springSort);

            // then
            assertThat(springSort.toString()).isEqualTo("id: ASC");
        }
    }

    @Nested
    class ConvertQuery {
        @Test
        @DisplayName("should able to convert empty query")
        void shouldAbleToConvertEmptyQuery() {
            // given
            final Query query = Query.builder().build();

            // when
            final org.springframework.data.relational.core.query.Query springQuery =
                    SpringQueries.convert(query, emptySet(), emptySet());
            log.info("springQuery: {}", springQuery);

            // when
            assertAll(
                    () -> assertThat(springQuery.getCriteria().orElseThrow().toString()).isBlank(),
                    () -> assertThat(springQuery.getSort()).isEqualTo(Sort.unsorted()),
                    () -> assertThat(springQuery.getLimit()).isEqualTo(Query.DEFAULT_SIZE),
                    () -> assertThat(springQuery.getOffset()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("should not set the offset if page size is not specified")
        void shouldNotSetTheOffsetIfPageSizeIsNotSpecified() {
            // given
            final Query query = Query.builder()
                    .page(10)
                    .build();

            // when
            final org.springframework.data.relational.core.query.Query springQuery =
                    SpringQueries.convert(query, emptySet(), emptySet());
            log.info("springQuery: {}", springQuery);

            // then
            assertAll(
                    () -> assertThat(springQuery.getCriteria().orElseThrow().toString()).isBlank(),
                    () -> assertThat(springQuery.getSort()).isEqualTo(Sort.unsorted()),
                    () -> assertThat(springQuery.getLimit()).isEqualTo(Query.DEFAULT_SIZE),
                    () -> assertThat(springQuery.getOffset())
                            .isEqualTo((long) query.getPage() * Query.DEFAULT_SIZE)
            );
        }

        @DisplayName("should able to convert a query with correct offset")
        @ParameterizedTest(name = "{index} => when the page is \"{0}\", then the offset should be \"{1}\"")
        @CsvSource({
                "0,0",
                "1,50",
                "2,100",
                "10,500",
        })
        void shouldAbleToConvertAQueryWithCorrectOffset(final int page, final long expectedOffset) {
            // given
            final Query query = Query.builder()
                    .page(page)
                    .size(50)
                    .build();

            // when
            final org.springframework.data.relational.core.query.Query springQuery =
                    SpringQueries.convert(query, emptySet(), emptySet());
            log.info("springQuery: {}", springQuery);

            // then
            assertAll(
                    () -> assertThat(springQuery.getCriteria().orElseThrow().toString()).isBlank(),
                    () -> assertThat(springQuery.getSort()).isEqualTo(Sort.unsorted()),
                    () -> assertThat(springQuery.getLimit()).isEqualTo(query.getSize()),
                    () -> assertThat(springQuery.getOffset()).isEqualTo(expectedOffset)
            );
        }

        @Test
        @DisplayName("should able to convert complex query")
        void shouldAbleToConvertComplexQuery() {
            // given
            final Set<SearchCriteria> searchCriteria = Set.of(
                    new SearchCriteria("name", eq, "Peter"),
                    new SearchCriteria("age", lt, 10),
                    new SearchCriteria("reason", like, "*bug*"),
                    new SearchCriteria("id", in, List.of(1, 2))
            );
            final List<Order> orders = List.of(
                    Order.builder()
                            .fieldName("name")
                            .direction(Order.Direction.asc)
                            .build(),
                    Order.builder()
                            .fieldName("age")
                            .direction(Order.Direction.desc)
                            .build(),
                    Order.builder()
                            .fieldName("id")
                            .direction(Order.Direction.asc)
                            .build()
            );
            final Query query = Query.builder()
                    .searchCriteria(searchCriteria)
                    .orders(orders)
                    .page(2)
                    .size(50)
                    .build();
            final Set<String> searchableFieldNames = Set.of("id", "name", "age", "reason");
            final Set<String> sortableFieldNames = Set.of("id", "name", "age");

            // when
            final org.springframework.data.relational.core.query.Query springQuery =
                    SpringQueries.convert(query, searchableFieldNames, sortableFieldNames);
            log.info("springQuery: {}", springQuery);

            // when
            assertAll(
                    () -> assertThat(springQuery.getCriteria().orElseThrow().toString()).isNotBlank(),
                    () -> assertThat(springQuery.getSort().toString()).isEqualTo("name: ASC,age: DESC,id: ASC"),
                    () -> assertThat(springQuery.getLimit()).isEqualTo(query.getSize()),
                    () -> assertThat(springQuery.getOffset()).isEqualTo((long) query.getPage() * query.getSize())
            );
        }
    }

    private static void verifySingleCriteriaDefinition(
            final SearchCriteria searchCriteria,
            final CriteriaDefinition criteriaDefinition,
            final CriteriaDefinition.Comparator expectedComparator,
            final boolean expectedIgnoreCase,
            final Object expectedValue
    ) {
        assertAll(
                () -> assertThat(Objects.requireNonNull(criteriaDefinition.getColumn()).getReference())
                        .isEqualTo(searchCriteria.getFieldName()),
                () -> assertThat(criteriaDefinition.getComparator()).isEqualTo(expectedComparator),
                () -> assertThat(criteriaDefinition.getValue()).isEqualTo(expectedValue),
                () -> assertThat(criteriaDefinition.isIgnoreCase()).isEqualTo(expectedIgnoreCase)
        );
    }
}