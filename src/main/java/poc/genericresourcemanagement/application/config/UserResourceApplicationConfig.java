package poc.genericresourcemanagement.application.config;

import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.resource.id.UserResourceIdGenerator;
import poc.genericresourcemanagement.application.service.resource.validation.UserResourceValidator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

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
}
