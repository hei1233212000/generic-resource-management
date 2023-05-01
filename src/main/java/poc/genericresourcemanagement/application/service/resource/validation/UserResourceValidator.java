package poc.genericresourcemanagement.application.service.resource.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.application.service.common.UserComponent;
import poc.genericresourcemanagement.domain.model.UserDomainModel;

public class UserResourceValidator
        extends AbstractResourceValidator<UserDomainModel>
        implements UserComponent {
    public UserResourceValidator(final ObjectMapper objectMapper, final BeanValidationService beanValidationService) {
        super(objectMapper, beanValidationService);
    }

    @Override
    protected Class<UserDomainModel> getResourceDomainModelClass() {
        return UserDomainModel.class;
    }
}
