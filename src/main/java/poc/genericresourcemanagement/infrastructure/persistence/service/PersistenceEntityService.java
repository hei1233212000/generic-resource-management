package poc.genericresourcemanagement.infrastructure.persistence.service;

import java.util.*;

import static poc.genericresourcemanagement.infrastructure.persistence.PersistenceModels.extractSearchableFieldNames;
import static poc.genericresourcemanagement.infrastructure.persistence.PersistenceModels.extractSortableFieldNames;

public class PersistenceEntityService {
    private final Map<Class<?>, Set<String>> entityClass2SearchableFieldNames;
    private final Map<Class<?>, Set<String>> entityClass2SortableFieldNames;

    public PersistenceEntityService(final List<Class<?>> entityClasses) {
        entityClass2SearchableFieldNames = new HashMap<>();
        entityClass2SortableFieldNames = new HashMap<>();

        entityClasses.forEach(entityClass -> {
            entityClass2SearchableFieldNames.put(entityClass, extractSearchableFieldNames(entityClass));
            entityClass2SortableFieldNames.put(entityClass, extractSortableFieldNames(entityClass));
        });
    }

    public Set<String> findSearchableFieldNames(final Class<?> entityClass) {
        return entityClass2SearchableFieldNames.getOrDefault(entityClass, Collections.emptySet());
    }

    public Set<String> findSortableFieldNames(final Class<?> entityClass) {
        return entityClass2SortableFieldNames.getOrDefault(entityClass, Collections.emptySet());
    }
}
