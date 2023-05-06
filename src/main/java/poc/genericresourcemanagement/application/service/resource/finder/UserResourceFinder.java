package poc.genericresourcemanagement.application.service.resource.finder;

import lombok.RequiredArgsConstructor;
import poc.genericresourcemanagement.application.error.ValidationErrorException;
import poc.genericresourcemanagement.application.service.common.UserComponent;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.domain.model.UserDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.UserPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class UserResourceFinder implements ResourceFinder<UserDomainModel>, UserComponent {
    private final UserRepository userRepository;

    @Override
    public Flux<UserDomainModel> findResources() {
        return userRepository.findAll()
                .map(entity2DomainModel());
    }

    @Override
    public Mono<UserDomainModel> findResource(final ResourceType resourceType, final String id) {
        final long userId;
        try {
            userId = Long.parseLong(id);
        } catch(Exception e) {
            throw new ValidationErrorException(
                    List.of(String.format("'%s' is not a number", id))
            );
        }
        return userRepository.findById(userId)
                .map(entity2DomainModel());
    }

    private static Function<UserPersistenceEntity, UserDomainModel> entity2DomainModel() {
        return resource -> new UserDomainModel(
                resource.getId(),
                resource.getName(),
                resource.getAge(),
                resource.getVersion(),
                resource.getCreatedBy(),
                resource.getCreatedTime(),
                resource.getUpdatedBy(),
                resource.getUpdatedTime()
        );
    }
}
