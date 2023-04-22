package poc.genericresourcemanagement.application.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;

@RequiredArgsConstructor
@Getter
public class FailToChangeResourceRequestStatusException extends RuntimeException {
    private final Operation operation;
    private final ResourceDomainModel.ResourceType resourceType;
    private final long resourceRequestId;
    private final ResourceDomainModel.ResourceStatus currentStatus;

    public enum Operation {
        APPROVE
    }
}
