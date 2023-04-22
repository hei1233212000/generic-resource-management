package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourcePersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
public class ResourceService {
    private final TimeGenerator timeGenerator;
    private final ResourceRepository resourceRepository;
    private final ResourceIdGeneratorService resourceIdGeneratorService;
    private final ResourceCreationValidationService resourceCreationValidationService;
    private final ResourceValidationService resourceValidationService;

    public Flux<ResourceDomainModel> findResourceDomainModelsByType(
            final ResourceDomainModel.ResourceType type
    ) {
        return resourceRepository.findAllByType(type)
                .map(this::convert);
    }

    public Mono<ResourceDomainModel> findResourceDomainModelById(
            final ResourceDomainModel.ResourceType type,
            final long id
    ) {
        return findResource(type, id).map(this::convert);
    }

    public Mono<ResourceDomainModel> createResource(
            final CreateResourceRequest createResourceRequest
    ) {
        resourceCreationValidationService.validate(createResourceRequest);
        return resourceIdGeneratorService.generateResourceId(createResourceRequest.type())
                .map(newId -> convert(newId, createResourceRequest))
                .flatMap(resourceRepository::save)
                .map(this::convert);
    }

    public Mono<ResourceDomainModel> approveResource(
            final ResourceDomainModel.ResourceType type,
            final long resourceRequestId
    ) {
        return findResource(type, resourceRequestId)
                .doOnNext(resource -> resourceValidationService.validate(resource.getType(), resource.getContent()))
                .flatMap(resource -> resourceRepository.updateStatus(
                        resource.getType(),
                        resource.getId(),
                        resource.getVersion(),
                        ResourceDomainModel.ResourceStatus.APPROVED,
                        "approver",
                        timeGenerator.currentLocalDateTime()
                ))
                .flatMap(updatedCount -> {
                    if (updatedCount == 1) {
                        return findResource(type, resourceRequestId);
                    } else {
                        return Mono.error(new IllegalStateException("failed to update with update count " + updatedCount));
                    }
                })
                .map(this::convert);
    }

    private Mono<ResourcePersistenceEntity> findResource(final ResourceDomainModel.ResourceType type, final long id) {
        return resourceRepository.findByTypeAndId(type, id);
    }

    @SneakyThrows
    private ResourceDomainModel convert(final ResourcePersistenceEntity resourcePersistenceEntity) {
        return ResourceDomainModel.builder()
                .type(resourcePersistenceEntity.getType())
                .id(resourcePersistenceEntity.getId())
                .content(resourcePersistenceEntity.getContent())
                .reason(resourcePersistenceEntity.getReason())
                .status(resourcePersistenceEntity.getStatus())
                .version(resourcePersistenceEntity.getVersion())
                .createdBy(resourcePersistenceEntity.getCreatedBy())
                .createdTime(resourcePersistenceEntity.getCreatedTime())
                .updatedBy(resourcePersistenceEntity.getUpdatedBy())
                .updatedTime(resourcePersistenceEntity.getUpdatedTime())
                .build();
    }

    @SneakyThrows
    private ResourcePersistenceEntity convert(final Long newId, final CreateResourceRequest createResourceRequest) {
        final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
        return ResourcePersistenceEntity.builder()
                .type(createResourceRequest.type())
                .id(newId)
                .content(createResourceRequest.content())
                .reason(createResourceRequest.reason())
                .status(ResourceDomainModel.ResourceStatus.PENDING_APPROVAL)
                .createdBy(createResourceRequest.createdBy())
                .createdTime(currentLocalDateTime)
                .updatedBy(createResourceRequest.createdBy())
                .updatedTime(currentLocalDateTime)
                .build();
    }
}
