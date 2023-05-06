package poc.genericresourcemanagement.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.application.service.resource.finder.UserResourceFinder;
import poc.genericresourcemanagement.application.service.resource.creator.UserResourceCreator;
import poc.genericresourcemanagement.application.service.resource.validation.UserResourceValidator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.UserRepository;

public class UserResourceApplicationConfig {
    @Bean
    UserResourceValidator userResourceCreationValidator(
            final ObjectMapper objectMapper,
            final BeanValidationService beanValidationService
    ) {
        return new UserResourceValidator(objectMapper, beanValidationService);
    }

    @Bean
    UserResourceCreator userResourceCreator(
            final UserRepository userRepository,
            final ObjectMapper objectMapper
    ) {
        return new UserResourceCreator(objectMapper, userRepository);
    }

    @Bean
    UserResourceFinder userResourceFinder(final UserRepository userRepository) {
        return new UserResourceFinder(userRepository);
    }
}
