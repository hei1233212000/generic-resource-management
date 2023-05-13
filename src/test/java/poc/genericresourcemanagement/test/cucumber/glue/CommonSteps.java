package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.java8.En;
import poc.genericresourcemanagement.domain.model.ResourceType;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CommonSteps implements En {
    public CommonSteps() {
        ParameterType("resourceType", ".*",
                (String resourceType) -> ResourceType.valueOf(resourceType));
        ParameterType("intList", "\\[(.*)]",
                (String idListInString) -> Arrays.stream(idListInString.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList())
        );
    }
}
