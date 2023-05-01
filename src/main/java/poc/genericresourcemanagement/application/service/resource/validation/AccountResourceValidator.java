package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import poc.genericresourcemanagement.application.service.common.AccountComponent;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.domain.model.AccountDomainModel;

public class AccountResourceValidator
        extends AbstractResourceValidator<AccountDomainModel>
        implements AccountComponent {

    public AccountResourceValidator(
            final ObjectMapper objectMapper,
            final BeanValidationService beanValidationService
    ) {
        super(objectMapper, beanValidationService);
    }

    @Override
    protected Class<AccountDomainModel> getResourceDomainModelClass() {
        return AccountDomainModel.class;
    }
}
