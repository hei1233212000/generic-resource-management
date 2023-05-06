package poc.genericresourcemanagement.interfaces.rest.mapper;

import poc.genericresourcemanagement.application.service.common.UserComponent;
import poc.genericresourcemanagement.domain.model.UserDomainModel;
import poc.genericresourcemanagement.interfaces.model.UserDto;

public class UserResourceDomainModel2DtoMapper
        implements ResourceDomainModel2DtoMapper<UserDomainModel, UserDto>, UserComponent {
    @Override
    public UserDto domainModel2Dto(final UserDomainModel resourceDomainModel) {
        return new UserDto(
                resourceDomainModel.id(),
                resourceDomainModel.name(),
                resourceDomainModel.age(),
                resourceDomainModel.version(),
                resourceDomainModel.createdBy(),
                resourceDomainModel.createdTime(),
                resourceDomainModel.updatedBy(),
                resourceDomainModel.updatedTime()
        );
    }
}
