package poc.genericresourcemanagement.interfaces.config;

import org.springframework.context.annotation.Bean;
import poc.genericresourcemanagement.interfaces.rest.mapper.UserResourceDomainModel2DtoMapper;

public class UserResourceInterfaceConfig {
    @Bean
    UserResourceDomainModel2DtoMapper userResourceDomainModel2DtoMapper() {
        return new UserResourceDomainModel2DtoMapper();
    }
}
