package poc.genericresourcemanagement.interfaces.rest.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import poc.genericresourcemanagement.application.model.SearchCriteria;
import poc.genericresourcemanagement.application.model.SearchCriteria.SearchCriteriaOperator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static poc.genericresourcemanagement.application.model.SearchCriteria.SearchCriteriaOperator.*;

class SearchCriteriaUtilsTest {

    @DisplayName("should return null when the search criteria is not recognized")
    @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
    @ValueSource(strings = {
            "unknown",
            "unknown(field,value)",
            "eq()",
            "eq(field)",
            "eq(field,value",
            "in()",
            "in(field)",
            "in(field,)",
            "in(field,()",
            "in(name,())",
    })
    void shouldThrowErrorWhenTheSearchCriteriaIsNotRecognized(final String input) {
        assertThat(SearchCriteriaUtils.parse(input)).isNull();
    }

    @Nested
    class Equal {
        @DisplayName("should able to parse the equal search by custom syntax")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "eq(name,Peter)",
                /*  with space outside the search criteria*/
                " eq(name,Peter)",
                "eq(name,Peter) ",
                " eq(name,Peter) ",
                /*  with space in field name */
                "eq( name,Peter)",
                "eq(name ,Peter)",
                "eq( name ,Peter)",
                /*  with space in value */
                "eq(name, Peter)",
                "eq(name,Peter )",
                "eq(name, Peter )",
        })
        void shouldAbleToParseTheEqualSearchByCustomSyntax(final String input) {
            verifyEqualSearchCriteria(input);
        }

        @Test
        @DisplayName("should able to parse the equal search by custom syntax even the value has spaces")
        void shouldAbleToParseTheEqualSearchByCustomSyntaxEvenTheValueHasSpaces() {
            verifyEqualSearchCriteria("eq(name,Peter Wong)", "Peter Wong");
        }

        @Test
        @DisplayName("should able to parse the equal search by custom syntax even the value has comma")
        void shouldAbleToParseTheEqualSearchByCustomSyntaxEvenTheValueHasComma() {
            verifyEqualSearchCriteria("eq(name,A,B)", "A,B");
        }

        @Test
        @DisplayName("should able to parse the equal search by custom syntax even the value has comma and space")
        void shouldAbleToParseTheEqualSearchByCustomSyntaxEvenTheValueHasCommaAndSpace() {
            verifyEqualSearchCriteria("eq(name,A,  B)", "A,  B");
        }

        @Test
        @DisplayName("should able to parse the equal search by standard query parameter")
        void shouldAbleToParseTheEqualSearchByStandardQueryParameter() {
            verifyEqualSearchCriteria("name=Peter");
        }

        private void verifyEqualSearchCriteria(final String input) {
            verifyEqualSearchCriteria(input, "Peter");
        }

        private void verifyEqualSearchCriteria(final String input, final String expectedValue) {
            verifySearchCriteria(input, "name", eq, expectedValue);
        }
    }

    @Nested
    class NotEqual {
        @DisplayName("should able to parse the not equal search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "ne(name,Peter)",
                /*  with space outside the search criteria*/
                " ne(name,Peter)",
                "ne(name,Peter) ",
                " ne(name,Peter) ",
                /*  with space in field name */
                "ne( name,Peter)",
                "ne(name ,Peter)",
                "ne( name ,Peter)",
                /*  with space in value */
                "ne(name, Peter)",
                "ne(name,Peter )",
                "ne(name, Peter )",
        })
        void shouldAbleToParseTheNotEqualSearch(final String input) {
            verifyNotEqualSearchCriteria(input, "Peter");
        }

        @Test
        @DisplayName("should able to parse the not equal search when the value has spaces")
        void shouldAbleToParseTheNotEqualSearchWhenTheValueHasSpaces() {
            verifyNotEqualSearchCriteria("ne(name,Peter Wong)", "Peter Wong");
        }

        @Test
        @DisplayName("should able to parse the not equal search when the value has comma")
        void shouldAbleToParseTheNotEqualSearchWhenTheValueHasComma() {
            verifyNotEqualSearchCriteria("ne(name,A,B)", "A,B");
        }

        @Test
        @DisplayName("should able to parse the not equal search when the value has comma and space")
        void shouldAbleToParseTheNotEqualSearchWhenTheValueHasCommaAndSpace() {
            verifyNotEqualSearchCriteria("ne(name,A,  B)", "A,  B");
        }

        private void verifyNotEqualSearchCriteria(final String input, final String expectedValue) {
            verifySearchCriteria(input, "name", ne, expectedValue);
        }
    }

    @Nested
    class GreaterThan {
        @DisplayName("should able to parse the greater than search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "gt(age,1.23)",
                /*  with space outside the search criteria*/
                " gt(age,1.23)",
                "gt(age,1.23) ",
                " gt(age,1.23) ",
                /*  with space in field name */
                "gt( age,1.23)",
                "gt(age ,1.23)",
                "gt( age ,1.23)",
                /*  with space in value */
                "gt(age, 1.23)",
                "gt(age,1.23 )",
                "gt(age, 1.23 )",
        })
        void shouldAbleToParseTheGreaterThanSearch(final String input) {
            verifySearchCriteria(input, "age", gt, "1.23");
        }
    }

    @Nested
    class GreaterThanOrEqualTo {
        @DisplayName("should able to parse the greater than or equal to search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "ge(age,1.23)",
                /*  with space outside the search criteria*/
                " ge(age,1.23)",
                "ge(age,1.23) ",
                " ge(age,1.23) ",
                /*  with space in field name */
                "ge( age,1.23)",
                "ge(age ,1.23)",
                "ge( age ,1.23)",
                /*  with space in value */
                "ge(age, 1.23)",
                "ge(age,1.23 )",
                "ge(age, 1.23 )",
        })
        void shouldAbleToParseTheGreaterThanOrEqualToSearch(final String input) {
            verifySearchCriteria(input, "age", ge, "1.23");
        }
    }

    @Nested
    class LessThan {
        @DisplayName("should able to parse the less than search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "lt(age,1.23)",
                /*  with space outside the search criteria*/
                " lt(age,1.23)",
                "lt(age,1.23) ",
                " lt(age,1.23) ",
                /*  with space in field name */
                "lt( age,1.23)",
                "lt(age ,1.23)",
                "lt( age ,1.23)",
                /*  with space in value */
                "lt(age, 1.23)",
                "lt(age,1.23 )",
                "lt(age, 1.23 )",
        })
        void shouldAbleToParseTheLessThanSearch(final String input) {
            verifySearchCriteria(input, "age", lt, "1.23");
        }
    }

    @Nested
    class LessThanOrEqualTo {
        @DisplayName("should able to parse the less than or equal to search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "le(age,1.23)",
                /*  with space outside the search criteria*/
                " le(age,1.23)",
                "le(age,1.23) ",
                " le(age,1.23) ",
                /*  with space in field name */
                "le( age,1.23)",
                "le(age ,1.23)",
                "le( age ,1.23)",
                /*  with space in value */
                "le(age, 1.23)",
                "le(age,1.23 )",
                "le(age, 1.23 )",
        })
        void shouldAbleToParseTheLessThanOrEqualToSearch(final String input) {
            verifySearchCriteria(input, "age", le, "1.23");
        }
    }

    @Nested
    class Like {
        @DisplayName("should able to parse the like search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "like(name,Peter*)",
                /*  with space outside the search criteria*/
                " like(name,Peter*)",
                "like(name,Peter*) ",
                " like(name,Peter*) ",
                /*  with space in field name */
                "like( name,Peter*)",
                "like(name ,Peter*)",
                "like( name ,Peter*)",
                /*  with space in value */
                "like(name, Peter*)",
                "like(name,Peter* )",
                "like(name, Peter* )",
        })
        void shouldAbleToParseTheLikeSearch(final String input) {
            verifyLikeSearchCriteria(input, "Peter*");
        }

        @Test
        @DisplayName("should able to parse the like search when the value has spaces")
        void shouldAbleToParseTheLikeSearchWhenTheValueHasSpaces() {
            verifyLikeSearchCriteria("like(name,Peter W*)", "Peter W*");
        }

        @Test
        @DisplayName("should able to parse the like search when the value has comma")
        void shouldAbleToParseTheLikeSearchWhenTheValueHasComma() {
            verifyLikeSearchCriteria("like(name,A,B)", "A,B");
        }

        @Test
        @DisplayName("should able to parse the like search when the value has comma and space")
        void shouldAbleToParseTheLikeSearchWhenTheValueHasCommaAndSpace() {
            verifyLikeSearchCriteria("like(name,A,  B)", "A,  B");
        }

        private void verifyLikeSearchCriteria(final String input, final String expectedValue) {
            verifySearchCriteria(input, "name", like, expectedValue);
        }
    }

    @Nested
    class NotLike {
        @DisplayName("should able to parse the ilike search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "ilike(name,Peter*)",
                /*  with space outside the search criteria*/
                " ilike(name,Peter*)",
                "ilike(name,Peter*) ",
                " ilike(name,Peter*) ",
                /*  with space in field name */
                "ilike( name,Peter*)",
                "ilike(name ,Peter*)",
                "ilike( name ,Peter*)",
                /*  with space in value */
                "ilike(name, Peter*)",
                "ilike(name,Peter* )",
                "ilike(name, Peter* )",
        })
        void shouldAbleToParseTheNotLikeSearch(final String input) {
            verifyNotLikeSearchCriteria(input, "Peter*");
        }

        @Test
        @DisplayName("should able to parse the ilike search when the value has spaces")
        void shouldAbleToParseTheNotLikeSearchWhenTheValueHasSpaces() {
            verifyNotLikeSearchCriteria("ilike(name,Peter W*)", "Peter W*");
        }

        @Test
        @DisplayName("should able to parse the ilike search when the value has comma")
        void shouldAbleToParseTheLikeSearchWhenTheValueHasComma() {
            verifyNotLikeSearchCriteria("ilike(name,A,B)", "A,B");
        }

        @Test
        @DisplayName("should able to parse the ilike search when the value has comma and space")
        void shouldAbleToParseTheLikeSearchWhenTheValueHasCommaAndSpace() {
            verifyNotLikeSearchCriteria("ilike(name,A,  B)", "A,  B");
        }

        private void verifyNotLikeSearchCriteria(final String input, final String expectedValue) {
            verifySearchCriteria(input, "name", ilike, expectedValue);
        }
    }

    @Nested
    class Contain {
        @DisplayName("should able to parse the contain search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "in(name,(Peter,Mary))",
                /*  with space outside the search criteria*/
                " in(name,(Peter,Mary))",
                "in(name,(Peter,Mary)) ",
                " in(name,(Peter,Mary)) ",
                /*  with space in field name */
                "in(name ,(Peter,Mary))",
                "in( name,(Peter,Mary))",
                "in( name ,(Peter,Mary))",
                /*  with space in value list */
                "in(name, (Peter,Mary))",
                "in(name,(Peter,Mary)) ",
                "in(name, (Peter,Mary)) ",
                /* with space between values */
                "in(name,( Peter,Mary))",
                "in(name,(Peter ,Mary))",
                "in(name,(Peter, Mary))",
                "in(name,(Peter,Mary ))",
                "in(name,( Peter ,Mary))",
                "in(name,(Peter, Mary ))",
                "in(name,( Peter , Mary ))",
        })
        void shouldAbleToParseTheContainSearch(final String input) {
            verifySearchCriteria(input, in, Set.of("Peter", "Mary"));
        }
    }

    @Nested
    class NotContain {
        @DisplayName("should able to parse the not contain search")
        @ParameterizedTest(name = "{index} => when the input is \"{0}\"")
        @ValueSource(strings = {
                "out(name,(Peter,Mary))",
                /*  with space outside the search criteria*/
                " out(name,(Peter,Mary))",
                "out(name,(Peter,Mary)) ",
                " out(name,(Peter,Mary)) ",
                /*  with space in field name */
                "out(name ,(Peter,Mary))",
                "out( name,(Peter,Mary))",
                "out( name ,(Peter,Mary))",
                /*  with space in value list */
                "out(name, (Peter,Mary))",
                "out(name,(Peter,Mary)) ",
                "out(name, (Peter,Mary)) ",
                /* with space between values */
                "out(name,( Peter,Mary))",
                "out(name,(Peter ,Mary))",
                "out(name,(Peter, Mary))",
                "out(name,(Peter,Mary ))",
                "out(name,( Peter ,Mary))",
                "out(name,(Peter, Mary ))",
                "out(name,( Peter , Mary ))",
        })
        void shouldAbleToParseTheNotContainSearch(final String input) {
            verifySearchCriteria(input, out, Set.of("Peter", "Mary"));
        }
    }

    private void verifySearchCriteria(
            final String input,
            final String expectedFieldName,
            final SearchCriteriaOperator expectedOperator,
            final String expectedValue
    ) {
        // when
        final SearchCriteria searchCriteria = SearchCriteriaUtils.parse(input);

        // then
        assertThat(searchCriteria).isNotNull();
        assertAll(
                () -> assertThat(searchCriteria.getFieldName()).isEqualTo(expectedFieldName),
                () -> assertThat(searchCriteria.getOperator()).isEqualTo(expectedOperator),
                () -> assertThat(searchCriteria.getValue()).isEqualTo(expectedValue)
        );
    }

    @SuppressWarnings("unchecked")
    private void verifySearchCriteria(
            final String input,
            final SearchCriteriaOperator expectedOperator,
            final Set<String> expectedValue
    ) {
        // when
        final SearchCriteria searchCriteria = SearchCriteriaUtils.parse(input);

        // then
        assertThat(searchCriteria).isNotNull();
        assertAll(
                () -> assertThat(searchCriteria.getFieldName()).isEqualTo("name"),
                () -> assertThat(searchCriteria.getOperator()).isEqualTo(expectedOperator),
                () -> assertThat(searchCriteria.getValue()).isInstanceOf(Set.class),
                () -> assertThat((Set<String>) searchCriteria.getValue())
                        .containsExactlyInAnyOrderElementsOf(expectedValue)
        );
    }
}