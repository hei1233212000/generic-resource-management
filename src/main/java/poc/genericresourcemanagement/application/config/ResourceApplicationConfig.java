package poc.genericresourcemanagement.application.config;

import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.ResourceService;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

public class ResourceApplicationConfig {
    @Bean
    ResourceService resourceService(final ResourceRepository resourceRepository) {
        return new ResourceService(resourceRepository);
    }
}
