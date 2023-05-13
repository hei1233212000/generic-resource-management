package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import poc.genericresourcemanagement.application.model.Pageable;
import poc.genericresourcemanagement.application.model.Query;
import poc.genericresourcemanagement.application.util.SpringQueries;
import poc.genericresourcemanagement.domain.model.DomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.PersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.service.PersistenceEntityService;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class QueryService {
    private final R2dbcEntityOperations r2dbcEntityOperations;
    public final PersistenceEntityService persistenceEntityService;

    public <DOMAIN extends DomainModel> Mono<Pageable<DOMAIN>> query(
            final Class<? extends PersistenceEntity> entityClass,
            final Query query,
            final Function<PersistenceEntity, DOMAIN> entity2DomainFunction
    ) {
        final Set<String> searchableFieldNames =
                persistenceEntityService.findSearchableFieldNames(entityClass);
        final Set<String> sortableFieldNames =
                persistenceEntityService.findSortableFieldNames(entityClass);

        final org.springframework.data.relational.core.query.Query actualQuery =
                SpringQueries.convert(query, searchableFieldNames, sortableFieldNames);
        final org.springframework.data.relational.core.query.Query findAllQuery = actualQuery.limit(-1).offset(-1);
        final ReactiveSelectOperation.TerminatingSelect<? extends PersistenceEntity> actuallySelect =
                r2dbcEntityOperations.select(entityClass)
                        .matching(actualQuery);
        final ReactiveSelectOperation.TerminatingSelect<? extends PersistenceEntity> findAllSelect =
                r2dbcEntityOperations.select(entityClass)
                        .matching(findAllQuery);
        return actuallySelect.all()
                .map(entity2DomainFunction)
                .collectList()
                .zipWith(findAllSelect.count())
                .map(t -> new Pageable<>(
                        query.getPage(), query.getSize(), t.getT1().size(), t.getT2(), t.getT1())
                );
    }
}
