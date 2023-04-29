package poc.genericresourcemanagement.application.service.resource.creator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import poc.genericresourcemanagement.domain.model.ResourceDomainModel;
import poc.genericresourcemanagement.infrastructure.persistence.model.UserPersistenceEntity;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Log4j2
public class UserResourceCreator implements ResourceCreator {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public boolean isSupported(final ResourceDomainModel.ResourceType resourceType) {
        return resourceType == ResourceDomainModel.ResourceType.USER;
    }

    @Override
    public Mono<Boolean> create(final JsonNode content, final String createdBy, final LocalDateTime createdTime) {
        final UserPersistenceEntity user = objectMapper.convertValue(content, UserPersistenceEntity.class);
        return userRepository.findUserNextId()
                .map(nextId -> {
                    user.setId(nextId);
                    user.setCreatedBy(createdBy);
                    user.setCreatedTime(createdTime);
                    user.setUpdatedBy(createdBy);
                    user.setUpdatedTime(createdTime);
                    return user;
                })
                .flatMap(userRepository::save)
                .map(u -> true);
    }
}
