package poc.genericresourcemanagement.infrastructure.persistence.config;

import io.github.daggerok.liquibase.r2dbc.LiquibaseR2dbcAutoConfiguration;
import org.springframework.context.annotation.Import;

@Import({
        LiquibaseR2dbcAutoConfiguration.class,
})
public class ResourcePersistenceConfig {
}
