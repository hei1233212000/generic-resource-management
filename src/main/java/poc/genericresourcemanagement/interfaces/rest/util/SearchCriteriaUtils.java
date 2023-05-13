package poc.genericresourcemanagement.interfaces.rest.util;

import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.application.model.SearchCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class SearchCriteriaUtils {
    private static final Pattern PATTERN_COMPARISON =
            Pattern.compile("\\s*(eq|ne|gt|ge|lt|le|like|ilike)\\((.*)\\)\\s*");
    private static final Pattern PATTERN_LIST = Pattern.compile("\\s*(in|out)\\((.*)\\s*,\\s*\\((.*)\\)\\s*\\)\\s*");
    private static final Pattern PATTERN_STANDARD_QUERY_PARAMETER = Pattern.compile("\\s*(.*)=(.*)\\s*");

    public static SearchCriteria parse(final String searchCriteria) {
        final Matcher comporisonSearchCriteriaMatcher = PATTERN_COMPARISON.matcher(searchCriteria);
        final Matcher listSearchCriteriaMatcher = PATTERN_LIST.matcher(searchCriteria);
        final Matcher standardEqMatcher = PATTERN_STANDARD_QUERY_PARAMETER.matcher(searchCriteria);
        if(comporisonSearchCriteriaMatcher.matches()) {
            final SearchCriteria.SearchCriteriaOperator operator =
                    SearchCriteria.SearchCriteriaOperator.valueOf(comporisonSearchCriteriaMatcher.group(1));
            final String values = comporisonSearchCriteriaMatcher.group(2);
            final String[] tokens = values.split(",", 2);
            if(tokens.length < 2) {
                log.debug(
                        "Invalid search criteria that we expected the comparison operator should have 2 tokens - searchCriteria: {}",
                        searchCriteria
                );
                return null;
            } else {
                return new SearchCriteria(tokens[0].trim(), operator, tokens[1].trim());
            }
        }
        if(listSearchCriteriaMatcher.matches()) {
            final SearchCriteria.SearchCriteriaOperator operator =
                    SearchCriteria.SearchCriteriaOperator.valueOf(listSearchCriteriaMatcher.group(1));
            final String fieldName = listSearchCriteriaMatcher.group(2);
            final String value = listSearchCriteriaMatcher.group(3);
            final Set<String> values = Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            if(values.isEmpty()) {
                log.debug(
                        "Invalid search criteria that we expected the list operator should not have empty string - searchCriteria: {}",
                        searchCriteria
                );
                return null;
            } else {
                return new SearchCriteria(fieldName.trim(), operator, values);
            }
        }
        if(standardEqMatcher.matches()) {
            final String fieldName = standardEqMatcher.group(1);
            final String value = standardEqMatcher.group(2);
            return new SearchCriteria(fieldName, SearchCriteria.SearchCriteriaOperator.eq, value);
        }
        log.debug("Invalid searchCriteria: {}", searchCriteria);
        return null;
    }
}
