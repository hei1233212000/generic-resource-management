package poc.genericresourcemanagement.application.service.resource.finder;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.service.common.UserComponent;
import poc.genericresourcemanagement.domain.model.UserDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class UserResourceFinder implements ResourceFinder<UserDomainModel>, UserComponent {
    private final UserRepository userRepository;

    @Override
    public Flux<UserDomainModel> findResources() {
        return userRepository.findAll()
                .map(resource -> new UserDomainModel(
                        resource.getId(),
                        resource.getName(),
                        resource.getAge(),
                        resource.getVersion(),
                        resource.getCreatedBy(),
                        resource.getCreatedTime(),
                        resource.getUpdatedBy(),
                        resource.getUpdatedTime()
                ));
    }
}
