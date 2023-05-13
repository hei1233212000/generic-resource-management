package poc.genericresourcemanagement.application.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.application.service.common.DefaultTimeGenerator;
import poc.genericresourcemanagement.application.service.common.TimeGenerator;
import poc.genericresourcemanagement.application.service.resource.*;
import poc.genericresourcemanagement.application.service.resource.creator.ResourceCreator;
import poc.genericresourcemanagement.application.service.resource.finder.ResourceFinder;
import poc.genericresourcemanagement.application.service.resource.validation.ResourceValidator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.ResourceRequestRepository;
import poc.genericresourcemanagement.infrastructure.persistence.service.PersistenceEntityService;

import java.util.List;

@Log4j2
@Import({
        UserResourceApplicationConfig.class,
        AccountResourceApplicationConfig.class
})
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
            final ResourceRequestCreationValidationService resourceRequestCreationValidationService,
            final ResourceRequestValidationService resourceRequestValidationService,
            final ResourceCreationService resourceCreationService,
            final QueryService queryService
    ) {
        return new ResourceRequestService(
                timeGenerator, resourceRequestRepository,
                resourceRequestCreationValidationService,
                resourceRequestValidationService, resourceCreationService,
                queryService
        );
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

    @Bean
    ResourceService resourceService(
            final QueryService queryService,
            final List<ResourceFinder> resourceFinders
    ) {
        return new ResourceService(queryService, resourceFinders);
    }

    @Bean
    QueryService queryService(
            final R2dbcEntityOperations r2dbcEntityOperations,
            final PersistenceEntityService persistenceEntityService
    ) {
        return new QueryService(r2dbcEntityOperations, persistenceEntityService);
    }
}
