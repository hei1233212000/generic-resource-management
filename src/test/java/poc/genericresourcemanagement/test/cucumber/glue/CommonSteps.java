package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.java8.En;
import poc.genericresourcemanagement.domain.model.ResourceType;

@SuppressWarnings("unused")
public class CommonSteps implements En {
    public CommonSteps() {
        ParameterType("resourceType", ".*",
                (String resourceType) -> ResourceType.valueOf(resourceType));
    }
}
