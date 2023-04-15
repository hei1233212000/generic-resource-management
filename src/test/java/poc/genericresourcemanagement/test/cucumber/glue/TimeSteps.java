package poc.genericresourcemanagement.test.cucumber.glue;

import io.cucumber.java8.En;
import poc.genericresourcemanagement.test.cucumber.service.ManualTimeGenerator;

@SuppressWarnings("unused")
public class TimeSteps implements En {
    public TimeSteps(final ManualTimeGenerator manualTimeGenerator) {
        Given("the current time is {string}", manualTimeGenerator::setCurrentLocalDateTime);
    }
}
