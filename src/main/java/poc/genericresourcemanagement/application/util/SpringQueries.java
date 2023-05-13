package poc.genericresourcemanagement.application.util;

import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import poc.genericresourcemanagement.application.model.Order;
import poc.genericresourcemanagement.application.model.Query;
import poc.genericresourcemanagement.application.model.SearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.relational.core.query.Query.query;

public class SpringQueries {
    public static org.springframework.data.relational.core.query.Query convert(
            final Query query,
            final Set<String> searchableFieldNames,
            final Set<String> sortableFieldNames
    ) {
        final CriteriaDefinition criteriaDefinition = convert(query.getSearchCriteria(), searchableFieldNames);
        org.springframework.data.relational.core.query.Query springQuery = query(criteriaDefinition);
        if(!query.getOrders().isEmpty()) {
            final Sort springSort = convert(query.getOrders(), sortableFieldNames);
            springQuery = springQuery.sort(springSort);
        }
        springQuery = springQuery.limit(query.getSize());
        springQuery = springQuery.offset((long) query.getSize() * query.getPage());
        return springQuery;
    }

    // visible for testing
    static CriteriaDefinition convert(
            final Collection<SearchCriteria> searchCriteria,
            final Set<String> searchableFieldNames
    ) {
        return searchCriteria.stream()
                .filter(sc -> searchableFieldNames.contains(sc.getFieldName()))
                .map(SpringQueries::convert)
                .reduce(Criteria.empty(), Criteria::and);
    }

    // visible for testing
    static Sort convert(final List<Order> orders, final Set<String> sortableFieldNames) {
        final List<Sort.Order> springOrders = orders.stream()
                .filter(order -> sortableFieldNames.contains(order.getFieldName()))
                .map(SpringQueries::convert)
                .collect(Collectors.toList());
        return Sort.by(springOrders);
    }

    // visible for testing
    static Criteria convert(final SearchCriteria searchCriteria) {
        final Criteria.CriteriaStep where = Criteria.where(searchCriteria.getFieldName());
        switch(searchCriteria.getOperator()) {
            case eq -> {return where.is(searchCriteria.getValue()).ignoreCase(true);}
            case ne -> {return where.not(searchCriteria.getValue()).ignoreCase(true);}
            case gt -> {return where.greaterThan(searchCriteria.getValue());}
            case ge -> {return where.greaterThanOrEquals(searchCriteria.getValue());}
            case lt -> {return where.lessThan(searchCriteria.getValue());}
            case le -> {return where.lessThanOrEquals(searchCriteria.getValue());}
            case like -> {return where.like(getValueForLikeSearch(searchCriteria)).ignoreCase(true);}
            case ilike -> {return where.notLike(getValueForLikeSearch(searchCriteria)).ignoreCase(true);}
            case in -> {return where.in((Collection<?>) searchCriteria.getValue());}
            case out -> {return where.notIn((Collection<?>) searchCriteria.getValue());}
        }
        throw new IllegalArgumentException("Unknown operator: " + searchCriteria.getOperator());
    }

    // visible for testing
    static Sort.Order convert(final Order order) {
        final Sort.Direction direction =
                order.getDirection() == Order.Direction.desc ? Sort.Direction.DESC : Sort.Direction.ASC;
        return new Sort.Order(direction, order.getFieldName());
    }

    private static Object getValueForLikeSearch(final SearchCriteria searchCriteria) {
        if(searchCriteria.getValue() instanceof String stringValue) {
            return stringValue.replaceAll("\\*", "%");
        }
        return searchCriteria.getValue();
    }
}
