package poc.genericresourcemanagement.application.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.application.service.common.DefaultTimeGenerator;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.application.service.resource.ResourceCreationValidationService;
import poc.genericresourcemanagement.application.service.resource.ResourceIdGeneratorService;
import poc.genericresourcemanagement.application.service.resource.ResourceService;
import poc.genericresourcemanagement.application.service.resource.id.ResourceIdGenerator;
import poc.genericresourcemanagement.application.service.resource.validation.ResourceCreationValidator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRepository;

import java.util.List;

@Log4j2
@Import({UserResourceApplicationConfig.class})
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
            final ResourceIdGeneratorService resourceIdGeneratorService,
            final ResourceCreationValidationService resourceCreationValidationService
    ) {
        return new ResourceService(
                timeGenerator, resourceRepository, resourceIdGeneratorService, resourceCreationValidationService
        );
    }

    @Bean
    ResourceIdGeneratorService resourceIdGeneratorService(
            final List<ResourceIdGenerator> resourceIdGenerators
    ) {
        return new ResourceIdGeneratorService(resourceIdGenerators);
    }

    @Bean
    ResourceCreationValidationService resourceCreationValidationService(
            final BeanValidationService beanValidationService,
            final List<ResourceCreationValidator> validators
    ) {
        return new ResourceCreationValidationService(beanValidationService, validators);
    }

    @Bean
    BeanValidationService beanValidationService(final jakarta.validation.Validator beanValidator) {
        return new BeanValidationService(beanValidator);
    }
}
