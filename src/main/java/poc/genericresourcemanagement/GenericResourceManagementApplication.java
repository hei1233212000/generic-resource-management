package poc.genericresourcemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import poc.genericresourcemanagement.application.config.ApplicationConfig;
import poc.genericresourcemanagement.infrastructure.persistence.config.PersistenceConfig;
import poc.genericresourcemanagement.interfaces.config.InterfaceConfig;

@SpringBootApplication
@Import({
        ApplicationConfig.class,
        InterfaceConfig.class,
        PersistenceConfig.class,
})
public class GenericResourceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenericResourceManagementApplication.class, args);
    }

}
