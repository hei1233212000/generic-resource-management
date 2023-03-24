package poc.genericresourcemanagement.application.service;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourcePersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public Flux<ResourceDomainModel> findResourceDomainModelsByType(
            final ResourceDomainModel.ResourceType type
    ) {
        return resourceRepository.findAllByType(type)
                .map(this::convert);
    }

    public Mono<ResourceDomainModel> findResourceDomainModelById(
            final ResourceDomainModel.ResourceType type,
            final String id
    ) {
        return resourceRepository.findByTypeAndId(type, id)
                .map(this::convert);
    }

    private ResourceDomainModel convert(final ResourcePersistenceEntity resourcePersistenceEntity) {
        return ResourceDomainModel.builder()
                .type(resourcePersistenceEntity.getType())
                .id(resourcePersistenceEntity.getId())
                .content(resourcePersistenceEntity.getContent())
                .status(resourcePersistenceEntity.getStatus())
                .version(resourcePersistenceEntity.getVersion())
                .createdBy(resourcePersistenceEntity.getCreatedBy())
                .createdTime(resourcePersistenceEntity.getCreatedTime())
                .updatedBy(resourcePersistenceEntity.getUpdatedBy())
                .updatedTime(resourcePersistenceEntity.getUpdatedTime())
                .build();
    }
}
