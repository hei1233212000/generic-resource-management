package poc.genericresourcemanagement.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.application.service.common.BeanValidationService;
import poc.genericresourcemanagement.application.service.resource.creator.AccountResourceCreator;
import poc.genericresourcemanagement.application.service.resource.validation.AccountResourceValidator;
import poc.genericresourcemanagement.infrastructure.persistence.repository.AccountRepository;

public class AccountResourceApplicationConfig {
    @Bean
    AccountResourceCreator accountResourceCreator(
            final AccountRepository accountRepository,
            final ObjectMapper objectMapper
    ) {
        return new AccountResourceCreator(objectMapper, accountRepository);
    }

    @Bean
    AccountResourceValidator accountResourceValidator(
            final ObjectMapper objectMapper,
            final BeanValidationService beanValidationService
    ) {
        return new AccountResourceValidator(objectMapper, beanValidationService);
    }
}
