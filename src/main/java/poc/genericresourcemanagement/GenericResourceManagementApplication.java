package poc.genericresourcemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import poc.genericresourcemanagement.application.config.ResourceApplicationConfig;
import poc.genericresourcemanagement.infrastructure.persistence.config.ResourcePersistenceConfig;
import poc.genericresourcemanagement.interfaces.config.ResourceInterfaceConfig;
import poc.genericresourcemanagement.interfaces.rest.ResourceRouter;

@SpringBootApplication
@Import({
        ResourceApplicationConfig.class,
        ResourceInterfaceConfig.class,
        ResourcePersistenceConfig.class,
})
public class GenericResourceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenericResourceManagementApplication.class, args);
    }

}
