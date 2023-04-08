package poc.genericresourcemanagement.application.config;

import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.ResourceService;
import poc.genericresourcemanagement.application.service.TimeGenerator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

public class ResourceApplicationConfig {
    @Bean
    TimeGenerator timeGenerator() {
        return new TimeGenerator();
    }

    @Bean
    ResourceService resourceService(
            final TimeGenerator timeGenerator,
            final ResourceRepository resourceRepository
    ) {
        return new ResourceService(timeGenerator, resourceRepository);
    }
}
