package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import poc.genericresourcemanagement.application.error.FailToChangeResourceRequestStatusException;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.model.Operation;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.ResourceRequestPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
@RequiredArgsConstructor
@Transactional
public class ResourceRequestService {
    private final TimeGenerator timeGenerator;
    private final ResourceRequestRepository resourceRequestRepository;
    private final ResourceRequestIdGeneratorService resourceRequestIdGeneratorService;
    private final ResourceRequestCreationValidationService resourceRequestCreationValidationService;
    private final ResourceRequestValidationService resourceRequestValidationService;
    private final ResourceCreationService resourceCreationService;

    public Flux<ResourceRequestDomainModel> findResourceRequestDomainModelsByType(
            final ResourceRequestDomainModel.ResourceType type
    ) {
        return resourceRequestRepository.findAllByType(type)
                .map(this::convert2ResourceRequestDomainModel);
    }

    public Mono<ResourceRequestDomainModel> findResourceRequestDomainModelById(
            final ResourceRequestDomainModel.ResourceType type,
            final long id
    ) {
        return findResourceRequestDomainModel(type, id);
    }

    public Mono<ResourceRequestDomainModel> createResourceRequest(
            final CreateResourceRequest createResourceRequest
    ) {
        resourceRequestCreationValidationService.validate(createResourceRequest);
        return resourceRequestIdGeneratorService.generateResourceRequestId(createResourceRequest.type())
                .map(newId -> convert2ResourceRequestPersistenceEntity(newId, createResourceRequest))
                .flatMap(resourceRequestRepository::save)
                .map(this::convert2ResourceRequestDomainModel);
    }

    public Mono<ResourceRequestDomainModel> approveOrCancelResourceRequest(
            final ResourceRequestDomainModel.ResourceType type,
            final long resourceRequestId,
            final Operation operation
    ) {
        final String user = "approver";
        final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
        return findResourceRequestPersistenceEntity(type, resourceRequestId)
                .doOnNext(validateApproveOrCancelResourceRequest(operation))
                .doOnNext(validateApproveOrCancelResourceRequestContent(operation))
                .flatMap(createOrUpdateResource(operation, user, currentLocalDateTime))
                .flatMap(updateResourceRequest(operation, user, currentLocalDateTime))
                .flatMap(convertApproveOrCancelResourceRequestResult2ResourceRequestDomainModel(
                        type, resourceRequestId, operation
                ));
    }

    private Mono<ResourceRequestDomainModel> findResourceRequestDomainModel(
            final ResourceRequestDomainModel.ResourceType type, final long id
    ) {
        return findResourceRequestPersistenceEntity(type, id)
                .map(this::convert2ResourceRequestDomainModel);
    }

    private Mono<ResourceRequestPersistenceEntity> findResourceRequestPersistenceEntity(
            final ResourceRequestDomainModel.ResourceType type, final long id
    ) {
        return resourceRequestRepository.findByTypeAndId(type, id);
    }

    private static Consumer<ResourceRequestPersistenceEntity> validateApproveOrCancelResourceRequest(
            final Operation operation) {
        return resourceRequest -> {
            if(resourceRequest.getStatus() != ResourceRequestDomainModel.ResourceRequestStatus.PENDING_APPROVAL) {
                throw new FailToChangeResourceRequestStatusException(
                        operation,
                        resourceRequest.getType(),
                        resourceRequest.getId(),
                        resourceRequest.getStatus()
                );
            }
        };
    }

    private Consumer<ResourceRequestPersistenceEntity> validateApproveOrCancelResourceRequestContent(
            final Operation operation
    ) {
        return resourceRequest -> {
            if(operation == Operation.APPROVE) {
                resourceRequestValidationService.validate(resourceRequest.getType(), resourceRequest.getContent());
            }
        };
    }

    private Function<ResourceRequestPersistenceEntity, Mono<ResourceRequestPersistenceEntity>> createOrUpdateResource(
            final Operation operation, final String user, final LocalDateTime currentLocalDateTime
    ) {
        return resourceRequest -> {
            if(operation == Operation.APPROVE) {
                // TODO: how do we know it is a creation?
                return resourceCreationService.create(
                                resourceRequest.getType(), resourceRequest.getContent(), user, currentLocalDateTime
                        )
                        .map(result -> resourceRequest);
            }
            return Mono.just(resourceRequest);
        };
    }

    private Function<ResourceRequestPersistenceEntity, Mono<Integer>> updateResourceRequest(
            final Operation operation, final String user, final LocalDateTime currentLocalDateTime
    ) {
        return resourceRequest -> {
            final ResourceRequestDomainModel.ResourceRequestStatus resourceRequestStatus =
                    operation == Operation.APPROVE
                            ? ResourceRequestDomainModel.ResourceRequestStatus.APPROVED
                            : ResourceRequestDomainModel.ResourceRequestStatus.CANCELLED;
            return resourceRequestRepository.updateStatus(
                    resourceRequest.getType(),
                    resourceRequest.getId(),
                    resourceRequest.getVersion(),
                    resourceRequestStatus,
                    user,
                    currentLocalDateTime
            );
        };
    }

    private Function<Integer, Mono<ResourceRequestDomainModel>> convertApproveOrCancelResourceRequestResult2ResourceRequestDomainModel(
            final ResourceRequestDomainModel.ResourceType type, final long resourceRequestId, final Operation operation
    ) {
        return updatedCount -> {
            if(updatedCount == 1) {
                return findResourceRequestDomainModel(type, resourceRequestId);
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
    private ResourceRequestDomainModel convert2ResourceRequestDomainModel(
            final ResourceRequestPersistenceEntity resourceRequestPersistenceEntity) {
        return ResourceRequestDomainModel.builder()
                .type(resourceRequestPersistenceEntity.getType())
                .id(resourceRequestPersistenceEntity.getId())
                .content(resourceRequestPersistenceEntity.getContent())
                .reason(resourceRequestPersistenceEntity.getReason())
                .operation(resourceRequestPersistenceEntity.getOperation())
                .status(resourceRequestPersistenceEntity.getStatus())
                .version(resourceRequestPersistenceEntity.getVersion())
                .createdBy(resourceRequestPersistenceEntity.getCreatedBy())
                .createdTime(resourceRequestPersistenceEntity.getCreatedTime())
                .updatedBy(resourceRequestPersistenceEntity.getUpdatedBy())
                .updatedTime(resourceRequestPersistenceEntity.getUpdatedTime())
                .build();
    }

    @SneakyThrows
    private ResourceRequestPersistenceEntity convert2ResourceRequestPersistenceEntity(
            final Long newId, final CreateResourceRequest createResourceRequest
    ) {
        final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
        return ResourceRequestPersistenceEntity.builder()
                .type(createResourceRequest.type())
                .id(newId)
                .content(createResourceRequest.content())
                .reason(createResourceRequest.reason())
                .operation(ResourceRequestDomainModel.ResourceRequestOperation.CREATE)
                .status(ResourceRequestDomainModel.ResourceRequestStatus.PENDING_APPROVAL)
                .createdBy(createResourceRequest.createdBy())
                .createdTime(currentLocalDateTime)
                .updatedBy(createResourceRequest.createdBy())
                .updatedTime(currentLocalDateTime)
                .build();
    }
}
