package poc.genericresourcemanagement.application.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.common.DefaultTimeGenerator;
import poc.genericresourcemanagement.application.service.resource.ResourceIdGeneratorService;
import poc.genericresourcemanagement.application.service.resource.ResourceService;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.application.service.resource.id.ResourceIdGenerator;
import poc.genericresourcemanagement.application.service.resource.id.UserResourceIdGenerator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

import java.util.List;

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
            final ResourceRepository resourceRepository,
            final ResourceIdGeneratorService resourceIdGeneratorService
    ) {
        return new ResourceService(timeGenerator, resourceRepository, resourceIdGeneratorService);
    }

    @Bean
    ResourceIdGeneratorService resourceIdGeneratorService(
            final List<ResourceIdGenerator> resourceIdGenerators
    ) {
        return new ResourceIdGeneratorService(resourceIdGenerators);
    }

    @Bean
    UserResourceIdGenerator userResourceIdGenerator(
            final ResourceRepository resourceRepository
    ) {
        return new UserResourceIdGenerator(resourceRepository);
    }
}
