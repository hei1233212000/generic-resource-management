package poc.genericresourcemanagement.interfaces.rest.mapper;

import poc.genericresourcemanagement.application.service.common.AccountComponent;
import poc.genericresourcemanagement.domain.model.AccountDomainModel;
import poc.genericresourcemanagement.interfaces.model.AccountDto;

public class AccountResourceDomainModel2DtoMapper
        implements ResourceDomainModel2DtoMapper<AccountDomainModel, AccountDto>, AccountComponent {
    @Override
    public AccountDto domainModel2Dto(final AccountDomainModel resourceDomainModel) {
        return new AccountDto(
                resourceDomainModel.id(),
                resourceDomainModel.holder(),
                resourceDomainModel.amount(),
                resourceDomainModel.version(),
                resourceDomainModel.createdBy(),
                resourceDomainModel.createdTime(),
                resourceDomainModel.updatedBy(),
                resourceDomainModel.updatedTime()
        );
    }
}
