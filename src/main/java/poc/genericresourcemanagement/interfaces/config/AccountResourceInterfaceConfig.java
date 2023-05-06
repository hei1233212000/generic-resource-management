package poc.genericresourcemanagement.interfaces.config;

import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.interfaces.rest.mapper.AccountResourceDomainModel2DtoMapper;

public class AccountResourceInterfaceConfig {
    @Bean
    AccountResourceDomainModel2DtoMapper accountResourceDomainModel2DtoMapper() {
        return new AccountResourceDomainModel2DtoMapper();
    }
}
