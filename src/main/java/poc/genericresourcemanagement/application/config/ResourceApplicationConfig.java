package poc.genericresourcemanagement.application.config;

import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.ResourceService;

public class ResourceApplicationConfig {
    @Bean
    ResourceService resourceService() {
        return new ResourceService();
    }
}
