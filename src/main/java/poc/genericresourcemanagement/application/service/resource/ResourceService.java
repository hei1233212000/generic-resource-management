package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import poc.genericresourcemanagement.application.error.FailToChangeResourceRequestStatusException;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.model.Operation;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourcePersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
@RequiredArgsConstructor
@Transactional
public class ResourceService {
    private final TimeGenerator timeGenerator;
    private final ResourceRepository resourceRepository;
    private final ResourceIdGeneratorService resourceIdGeneratorService;
    private final ResourceCreationValidationService resourceCreationValidationService;
    private final ResourceValidationService resourceValidationService;
    private final ResourceCreationService resourceCreationService;

    public Flux<ResourceDomainModel> findResourceDomainModelsByType(
            final ResourceDomainModel.ResourceType type
    ) {
        return resourceRepository.findAllByType(type)
                .map(this::convert2ResourceDomainModel);
    }

    public Mono<ResourceDomainModel> findResourceDomainModelById(
            final ResourceDomainModel.ResourceType type,
            final long id
    ) {
        return findResourceResourceDomainModel(type, id);
    }

    public Mono<ResourceDomainModel> createResource(
            final CreateResourceRequest createResourceRequest
    ) {
        resourceCreationValidationService.validate(createResourceRequest);
        return resourceIdGeneratorService.generateResourceId(createResourceRequest.type())
                .map(newId -> convert2ResourcePersistenceEntity(newId, createResourceRequest))
                .flatMap(resourceRepository::save)
                .map(this::convert2ResourceDomainModel);
    }

    public Mono<ResourceDomainModel> approveOrCancelResource(
            final ResourceDomainModel.ResourceType type,
            final long resourceRequestId,
            final Operation operation
    ) {
        final String user = "approver";
        final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
        return findResourceResourcePersistenceEntity(type, resourceRequestId)
                .doOnNext(validateApproveOrCancelResourceRequest(operation))
                .doOnNext(validateApproveOrCancelResourceRequestContent(operation))
                .flatMap(createOrUpdateResource(operation, user, currentLocalDateTime))
                .flatMap(updateResourceRequest(operation, user, currentLocalDateTime))
                .flatMap(convertApproveOrCancelResourceRequestResult2ResourceDomainModel(
                        type, resourceRequestId, operation
                ));
    }

    private Mono<ResourceDomainModel> findResourceResourceDomainModel(
            final ResourceDomainModel.ResourceType type, final long id
    ) {
        return findResourceResourcePersistenceEntity(type, id)
                .map(this::convert2ResourceDomainModel);
    }

    private Mono<ResourcePersistenceEntity> findResourceResourcePersistenceEntity(
            final ResourceDomainModel.ResourceType type, final long id
    ) {
        return resourceRepository.findByTypeAndId(type, id);
    }

    private static Consumer<ResourcePersistenceEntity> validateApproveOrCancelResourceRequest(
            final Operation operation) {
        return resource -> {
            if(resource.getStatus() != ResourceDomainModel.ResourceStatus.PENDING_APPROVAL) {
                throw new FailToChangeResourceRequestStatusException(
                        operation,
                        resource.getType(),
                        resource.getId(),
                        resource.getStatus()
                );
            }
        };
    }

    private Consumer<ResourcePersistenceEntity> validateApproveOrCancelResourceRequestContent(
            final Operation operation
    ) {
        return resource -> {
            if(operation == Operation.APPROVE) {
                resourceValidationService.validate(resource.getType(), resource.getContent());
            }
        };
    }

    private Function<ResourcePersistenceEntity, Mono<ResourcePersistenceEntity>> createOrUpdateResource(
            final Operation operation, final String user, final LocalDateTime currentLocalDateTime
    ) {
        return resource -> {
            if(operation == Operation.APPROVE) {
                // TODO: how do we know it is a creation?
                return resourceCreationService.create(
                                resource.getType(), resource.getContent(), user, currentLocalDateTime
                        )
                        .map(result -> resource);
            }
            return Mono.just(resource);
        };
    }

    private Function<ResourcePersistenceEntity, Mono<Integer>> updateResourceRequest(
            final Operation operation, final String user, final LocalDateTime currentLocalDateTime
    ) {
        return resource -> {
            final ResourceDomainModel.ResourceStatus resourceStatus = operation == Operation.APPROVE
                    ? ResourceDomainModel.ResourceStatus.APPROVED
                    : ResourceDomainModel.ResourceStatus.CANCELLED;
            return resourceRepository.updateStatus(
                    resource.getType(),
                    resource.getId(),
                    resource.getVersion(),
                    resourceStatus,
                    user,
                    currentLocalDateTime
            );
        };
    }

    private Function<Integer, Mono<ResourceDomainModel>> convertApproveOrCancelResourceRequestResult2ResourceDomainModel(
            final ResourceDomainModel.ResourceType type, final long resourceRequestId, final Operation operation
    ) {
        return updatedCount -> {
            if(updatedCount == 1) {
                return findResourceResourceDomainModel(type, resourceRequestId);
            } else {
                log.error(
                        "failed to update the resource where type: {}, resourceRequestId: {}, operation: {}, updatedCount: {}",
                        type, resourceRequestId, operation, updatedCount
                );
                return Mono.error(
                        new IllegalStateException("failed to approve or cancel the resource"));
            }
        };
    }

    @SneakyThrows
    private ResourceDomainModel convert2ResourceDomainModel(final ResourcePersistenceEntity resourcePersistenceEntity) {
        return ResourceDomainModel.builder()
                .type(resourcePersistenceEntity.getType())
                .id(resourcePersistenceEntity.getId())
                .content(resourcePersistenceEntity.getContent())
                .reason(resourcePersistenceEntity.getReason())
                .operation(resourcePersistenceEntity.getOperation())
                .status(resourcePersistenceEntity.getStatus())
                .version(resourcePersistenceEntity.getVersion())
                .createdBy(resourcePersistenceEntity.getCreatedBy())
                .createdTime(resourcePersistenceEntity.getCreatedTime())
                .updatedBy(resourcePersistenceEntity.getUpdatedBy())
                .updatedTime(resourcePersistenceEntity.getUpdatedTime())
                .build();
    }

    @SneakyThrows
    private ResourcePersistenceEntity convert2ResourcePersistenceEntity(
            final Long newId, final CreateResourceRequest createResourceRequest
    ) {
        final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
        return ResourcePersistenceEntity.builder()
                .type(createResourceRequest.type())
                .id(newId)
                .content(createResourceRequest.content())
                .reason(createResourceRequest.reason())
                .operation(ResourceDomainModel.Operation.CREATE)
                .status(ResourceDomainModel.ResourceStatus.PENDING_APPROVAL)
                .createdBy(createResourceRequest.createdBy())
                .createdTime(currentLocalDateTime)
                .updatedBy(createResourceRequest.createdBy())
                .updatedTime(currentLocalDateTime)
                .build();
    }
}
