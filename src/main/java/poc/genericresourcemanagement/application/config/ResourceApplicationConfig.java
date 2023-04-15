package poc.genericresourcemanagement.application.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.DefaultTimeGenerator;
import poc.genericresourcemanagement.application.service.ResourceService;
import poc.genericresourcemanagement.application.service.TimeGenerator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

@Log4j2
public class ResourceApplicationConfig {
    @Bean
    @ConditionalOnMissingBean
    TimeGenerator timeGenerator() {
        log.info("Using DefaultTimeGenerator");
        return new DefaultTimeGenerator();
    }

    @Bean
    ResourceService resourceService(
            final TimeGenerator timeGenerator,
            final ResourceRepository resourceRepository
    ) {
        return new ResourceService(timeGenerator, resourceRepository);
    }
}
