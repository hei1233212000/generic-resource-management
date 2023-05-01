package poc.genericresourcemanagement.application.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.model.RequestOperation;
import poc.genericresourcemanagement.domain.model.ResourceRequestDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;

@RequiredArgsConstructor
@Getter
public class FailToChangeResourceRequestStatusException extends RuntimeException {
    private final RequestOperation requestOperation;
    private final ResourceType resourceType;
    private final long resourceRequestId;
    private final ResourceRequestDomainModel.ResourceRequestStatus currentStatus;

}
