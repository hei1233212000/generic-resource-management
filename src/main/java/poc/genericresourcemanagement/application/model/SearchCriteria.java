package poc.genericresourcemanagement.application.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
@RequiredArgsConstructor
public class SearchCriteria {
    public enum SearchCriteriaOperator {
        eq, ne, gt, ge, lt, le, like, ilike, in, out
    }

    private final String fieldName;
    private final SearchCriteriaOperator operator;
    private final Object value;
}
