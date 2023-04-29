package poc.genericresourcemanagement.application.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.model.Operation;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;

@RequiredArgsConstructor
@Getter
public class FailToChangeResourceRequestStatusException extends RuntimeException {
    private final Operation operation;
    private final ResourceRequestDomainModel.ResourceType resourceType;
    private final long resourceRequestId;
    private final ResourceRequestDomainModel.ResourceRequestStatus currentStatus;

}
