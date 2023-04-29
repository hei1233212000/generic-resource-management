package poc.genericresourcemanagement.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.resource.creator.UserResourceCreator;
import poc.genericresourcemanagement.application.service.resource.id.UserResourceIdGenerator;
import poc.genericresourcemanagement.application.service.resource.validation.UserResourceValidator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;

public class UserResourceApplicationConfig {
    @Bean
    UserResourceIdGenerator userResourceIdGenerator(
            final ResourceRepository resourceRepository
    ) {
        return new UserResourceIdGenerator(resourceRepository);
    }

    @Bean
    UserResourceValidator userResourceCreationValidator() {
        return new UserResourceValidator();
    }

    @Bean
    UserResourceCreator userResourceCreator(
            final UserRepository userRepository,
            final ObjectMapper objectMapper
    ) {
        return new UserResourceCreator(userRepository, objectMapper);
    }
}
