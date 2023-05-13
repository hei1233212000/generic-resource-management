package poc.genericresourcemanagement.infrastructure.persistence;

import poc.genericresourcemanagement.infrastructure.persistence.model.SearchableField;
import poc.genericresourcemanagement.infrastructure.persistence.model.SortableField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PersistenceModels {
    public static Set<String> extractSearchableFieldNames(final Class<?> clazz) {
        return extractFieldNames(clazz, SearchableField.class);
    }

    public static Set<String> extractSortableFieldNames(final Class<?> clazz) {
        return extractFieldNames(clazz, SortableField.class);
    }

    private static Set<String> extractFieldNames(
            final Class<?> clazz,
            final Class<? extends Annotation> expectedAnnotationClass
    ) {
        final Set<String> fieldNames = new HashSet<>();
        extractFieldNames(clazz, fieldNames, expectedAnnotationClass);
        return fieldNames;
    }

    private static void extractFieldNames(
            final Class<?> clazz,
            final Set<String> fieldNames,
            final Class<? extends Annotation> expectedAnnotationClass
    ) {
        Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(expectedAnnotationClass))
                .map(Field::getName)
                .forEach(fieldNames::add);
        if(clazz.getSuperclass() != null) {
            extractFieldNames(clazz.getSuperclass(), fieldNames, expectedAnnotationClass);
        }
    }
}
