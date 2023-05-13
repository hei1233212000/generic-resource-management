package poc.genericresourcemanagement.infrastructure.persistence;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourceRequestPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.model.SearchableField;
import poc.genericresourcemanagement.infrastructure.persistence.model.SortableField;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceModelsTest {
    @Test
    @DisplayName("should return empty Set if there is no searchable field")
    void shouldReturnEmptySetIfThereIsNoSearchableField() {
        assertThat(PersistenceModels.extractSearchableFieldNames(EmptyClass.class)).isEmpty();
    }

    @Test
    @DisplayName("should able to extract searchable field names")
    void shouldAbleToExtractSearchableFieldNames() {
        // when
        final Set<String> searchableFieldNames =
                PersistenceModels.extractSearchableFieldNames(ClassWithSearchableAndSortableFields.class);

        // then
        assertThat(searchableFieldNames).containsExactlyInAnyOrder("parentId", "id", "name");
    }

    @Test
    @DisplayName("should return empty Set if there is no sortable field")
    void shouldReturnEmptySetIfThereIsNoSortableField() {
        assertThat(PersistenceModels.extractSortableFieldNames(EmptyClass.class)).isEmpty();
    }

    @Test
    @DisplayName("should able to extract sortable field names")
    void shouldAbleToExtractSortableFieldNames() {
        // when
        final Set<String> sortableFieldNames =
                PersistenceModels.extractSortableFieldNames(ClassWithSearchableAndSortableFields.class);

        // then
        assertThat(sortableFieldNames).containsExactlyInAnyOrder("parentName", "age", "country");
    }

    @Test
    @DisplayName("should able to extract searchable field names and sortable field names from entity class")
    void shouldAbleToExtractSearchableFieldNamesAndSortableFieldNamesFromEntityClass() {
        // when
        final Set<String> searchableFieldNames =
                PersistenceModels.extractSearchableFieldNames(ResourceRequestPersistenceEntity.class);
        final Set<String> sortableFieldNames =
                PersistenceModels.extractSortableFieldNames(ResourceRequestPersistenceEntity.class);

        // then
        assertThat(searchableFieldNames)
                .containsExactlyInAnyOrder("createdBy", "createdTime", "updatedBy", "updatedTime", "type", "id",
                        "reason", "operation", "status");
        assertThat(sortableFieldNames)
                .containsExactlyInAnyOrder("createdTime", "updatedTime", "id");
    }

    @Data
    private static class EmptyClass {
        private String name;
    }

    @Data
    private static abstract class AbstractClass {
        @SearchableField
        private Integer parentId;

        @SortableField
        private String parentName;
        private Integer parentAge;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class ClassWithSearchableAndSortableFields extends AbstractClass {
        @SearchableField
        private Integer id;

        @SearchableField
        private String name;

        @SortableField
        private Integer age;

        @SortableField
        private String country;

        private String address;

    }
}