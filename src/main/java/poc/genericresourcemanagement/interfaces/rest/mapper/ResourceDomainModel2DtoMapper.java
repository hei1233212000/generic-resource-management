package poc.genericresourcemanagement.interfaces.rest.mapper;

import poc.genericresourcemanagement.application.service.common.ResourceSpecificComponent;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.interfaces.model.ResourceDto;

public interface ResourceDomainModel2DtoMapper<DOMAIN extends ResourceDomainModel, DTO extends ResourceDto>
        extends ResourceSpecificComponent {
    DTO domainModel2Dto(DOMAIN resourceDomainModel);
}
