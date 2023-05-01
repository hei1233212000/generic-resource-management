package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.domain.model.AccountDomainModel;
import poc.genericresourcemanagement.domain.model.ResourceType;

@RequiredArgsConstructor
public class AccountResourceValidator implements ResourceValidator {
    private final ObjectMapper objectMapper;
    private final BeanValidationService beanValidationService;

    @Override
    public boolean isSupported(final ResourceType resourceType) {
        return resourceType == ResourceType.ACCOUNT;
    }

    @Override
    public void validate(final JsonNode resource) {
        final AccountDomainModel accountDomainModel = objectMapper.convertValue(resource, AccountDomainModel.class);
        beanValidationService.validate(accountDomainModel);
    }
}
