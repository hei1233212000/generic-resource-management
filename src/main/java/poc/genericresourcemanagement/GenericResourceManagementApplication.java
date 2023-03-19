package poc.genericresourcemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import poc.genericresourcemanagement.application.config.ResourceApplicationConfig;
import poc.genericresourcemanagement.interfaces.rest.ResourceRouter;

@SpringBootApplication
@Import({
        ResourceApplicationConfig.class,
        ResourceRouter.class
})
public class GenericResourceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenericResourceManagementApplication.class, args);
    }

}
