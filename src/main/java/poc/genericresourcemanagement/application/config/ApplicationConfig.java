package poc.genericresourcemanagement.application.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.application.service.common.DefaultTimeGenerator;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.application.service.resource.*;
import poc.genericresourcemanagement.application.service.resource.creator.ResourceCreator;
import poc.genericresourcemanagement.application.service.resource.id.ResourceRequestIdGenerator;
import poc.genericresourcemanagement.application.service.resource.validation.ResourceValidator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;

import java.util.List;

@Log4j2
@Import({UserResourceApplicationConfig.class})
public class ApplicationConfig {
    @Bean
    @ConditionalOnMissingBean
    TimeGenerator timeGenerator() {
        log.info("Using DefaultTimeGenerator");
        return new DefaultTimeGenerator();
    }

    @Bean
    ResourceRequestService resourceRequestService(
            final TimeGenerator timeGenerator,
            final ResourceRequestRepository resourceRequestRepository,
            final ResourceRequestIdGeneratorService resourceRequestIdGeneratorService,
            final ResourceRequestCreationValidationService resourceRequestCreationValidationService,
            final ResourceRequestValidationService resourceRequestValidationService,
            final ResourceCreationService resourceCreationService
    ) {
        return new ResourceRequestService(
                timeGenerator, resourceRequestRepository, resourceRequestIdGeneratorService,
                resourceRequestCreationValidationService,
                resourceRequestValidationService, resourceCreationService
        );
    }

    @Bean
    ResourceRequestIdGeneratorService resourceRequestIdGeneratorService(
            final List<ResourceRequestIdGenerator> resourceRequestIdGenerators
    ) {
        return new ResourceRequestIdGeneratorService(resourceRequestIdGenerators);
    }

    @Bean
    ResourceRequestCreationValidationService resourceRequestCreationValidationService(
            final BeanValidationService beanValidationService,
            final ResourceRequestValidationService resourceRequestValidationService
    ) {
        return new ResourceRequestCreationValidationService(beanValidationService, resourceRequestValidationService);
    }

    @Bean
    ResourceRequestValidationService resourceRequestValidationService(
            final List<ResourceValidator> resourceValidators
    ) {
        return new ResourceRequestValidationService(resourceValidators);
    }

    @Bean
    BeanValidationService beanValidationService(final jakarta.validation.Validator beanValidator) {
        return new BeanValidationService(beanValidator);
    }

    @Bean
    ResourceCreationService resourceCreationService(final List<ResourceCreator> resourceCreators) {
        return new ResourceCreationService(resourceCreators);
    }
}
