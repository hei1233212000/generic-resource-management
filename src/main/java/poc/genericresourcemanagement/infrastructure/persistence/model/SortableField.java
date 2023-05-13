package poc.genericresourcemanagement.infrastructure.persistence.model;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SortableField {
}
