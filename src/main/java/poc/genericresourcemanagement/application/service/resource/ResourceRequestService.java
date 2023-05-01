package poc.genericresourcemanagement.application.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import poc.genericresourcemanagement.application.error.FailToChangeResourceRequestStatusException;
import poc.genericresourcemanagement.application.model.CreateResourceRequest;
import poc.genericresourcemanagement.application.model.RequestOperation;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;
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
    private final ResourceRequestCreationValidationService resourceRequestCreationValidationService;
    private final ResourceRequestValidationService resourceRequestValidationService;
    private final ResourceCreationService resourceCreationService;

    public Flux<ResourceRequestDomainModel> findResourceRequestDomainModelsByType(
            final ResourceType type
    ) {
        return resourceRequestRepository.findAllByType(type)
                .map(this::convert2ResourceRequestDomainModel);
    }

    public Mono<ResourceRequestDomainModel> findResourceRequestDomainModelById(
            final ResourceType type,
            final long id
    ) {
        return findResourceRequestDomainModel(type, id);
    }

    public Mono<ResourceRequestDomainModel> createResourceRequest(
            final CreateResourceRequest createResourceRequest
    ) {
        resourceRequestCreationValidationService.validate(createResourceRequest);
        return resourceRequestRepository.findNextResourceRequestId(createResourceRequest.type())
                .map(newId -> convert2ResourceRequestPersistenceEntity(newId, createResourceRequest))
                .flatMap(resourceRequestRepository::save)
                .map(this::convert2ResourceRequestDomainModel);
    }

    public Mono<ResourceRequestDomainModel> approveOrCancelResourceRequest(
            final ResourceType type,
            final long resourceRequestId,
            final RequestOperation requestOperation
    ) {
        final String user = "approver";
        final LocalDateTime currentLocalDateTime = timeGenerator.currentLocalDateTime();
        return findResourceRequestPersistenceEntity(type, resourceRequestId)
                .doOnNext(validateApproveOrCancelResourceRequest(requestOperation))
                .doOnNext(validateApproveOrCancelResourceRequestContent(requestOperation))
                .flatMap(createOrUpdateResource(requestOperation, user, currentLocalDateTime))
                .flatMap(updateResourceRequest(requestOperation, user, currentLocalDateTime))
                .flatMap(convertApproveOrCancelResourceRequestResult2ResourceRequestDomainModel(
                        type, resourceRequestId, requestOperation
                ));
    }

    private Mono<ResourceRequestDomainModel> findResourceRequestDomainModel(
            final ResourceType type, final long id
    ) {
        return findResourceRequestPersistenceEntity(type, id)
                .map(this::convert2ResourceRequestDomainModel);
    }

    private Mono<ResourceRequestPersistenceEntity> findResourceRequestPersistenceEntity(
            final ResourceType type, final long id
    ) {
        return resourceRequestRepository.findByTypeAndId(type, id);
    }

    private static Consumer<ResourceRequestPersistenceEntity> validateApproveOrCancelResourceRequest(
            final RequestOperation requestOperation) {
        return resourceRequest -> {
            if(resourceRequest.getStatus() != ResourceRequestDomainModel.ResourceRequestStatus.PENDING_APPROVAL) {
                throw new FailToChangeResourceRequestStatusException(
                        requestOperation,
                        resourceRequest.getType(),
                        resourceRequest.getId(),
                        resourceRequest.getStatus()
                );
            }
        };
    }

    private Consumer<ResourceRequestPersistenceEntity> validateApproveOrCancelResourceRequestContent(
            final RequestOperation requestOperation
    ) {
        return resourceRequest -> {
            if(requestOperation == RequestOperation.APPROVE) {
                resourceRequestValidationService.validate(resourceRequest.getType(), resourceRequest.getContent());
            }
        };
    }

    private Function<ResourceRequestPersistenceEntity, Mono<ResourceRequestPersistenceEntity>> createOrUpdateResource(
            final RequestOperation requestOperation, final String user, final LocalDateTime currentLocalDateTime
    ) {
        return resourceRequest -> {
            if(requestOperation == RequestOperation.APPROVE) {
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
            final RequestOperation requestOperation, final String user, final LocalDateTime currentLocalDateTime
    ) {
        return resourceRequest -> {
            final ResourceRequestDomainModel.ResourceRequestStatus resourceRequestStatus =
                    requestOperation == RequestOperation.APPROVE
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
            final ResourceType type, final long resourceRequestId, final RequestOperation requestOperation
    ) {
        return updatedCount -> {
            if(updatedCount == 1) {
                return findResourceRequestDomainModel(type, resourceRequestId);
            } else {
                log.error(
                        "failed to update the resource where type: {}, resourceRequestId: {}, operation: {}, updatedCount: {}",
                        type, resourceRequestId, requestOperation, updatedCount
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
