package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.application.service.common.UserComponent;
import poc.genericresourcemanagement.domain.model.ResourceType;
import poc.genericresourcemanagement.infrastructure.persistence.model.UserPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;
import reactor.core.publisher.Mono;

@Log4j2
public class UserResourceCreator extends AbstractResourceCreator<UserPersistenceEntity> implements UserComponent {
    private final UserRepository userRepository;

    public UserResourceCreator(final ObjectMapper objectMapper, final UserRepository userRepository) {
        super(objectMapper);
        this.userRepository = userRepository;
    }

    @Override
    protected Class<UserPersistenceEntity> getResourceClass() {
        return UserPersistenceEntity.class;
    }

    @Override
    protected Mono<Boolean> create(final UserPersistenceEntity user) {
        return userRepository.findUserNextId()
                .map(nextId -> {
                    user.setId(nextId);
                    return user;
                })
                .flatMap(userRepository::save)
                .map(u -> true);
    }
}
