package poc.genericresourcemanagement.infrastructure.persistence.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.daggerok.liquibase.r2dbc.LiquibaseR2dbcAutoConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import poc.genericresourcemanagement.infrastructure.persistence.model.converter.JsonNode2StringConverter;
import poc.genericresourcemanagement.infrastructure.persistence.model.converter.String2JsonNodeConverter;

import java.util.List;

@Import({
        LiquibaseR2dbcAutoConfiguration.class,
})
public class PersistenceConfig {
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(
            final ConnectionFactory connectionFactory,
            final ObjectMapper objectMapper
    ) {
        final List<Converter<?, ?>> converters = List.of(
                new JsonNode2StringConverter(objectMapper),
                new String2JsonNodeConverter(objectMapper)
        );
        final R2dbcDialect r2dbcDialect = DialectResolver.getDialect(connectionFactory);
        return R2dbcCustomConversions.of(r2dbcDialect, converters);
    }
}
