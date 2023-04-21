package poc.genericresourcemanagement.test.cucumber.service;

import poc.genericresourcemanagement.application.service.common.TimeGenerator;

import java.time.LocalDateTime;

import static poc.genericresourcemanagement.interfaces.config.LocalDateTimeJsonSerializer.DATE_TIME_FORMATTER;

public class ManualTimeGenerator implements TimeGenerator {
    private LocalDateTime currentLocalDateTime;

    public void setCurrentLocalDateTime(String timeInString) {
        currentLocalDateTime = LocalDateTime.parse(timeInString, DATE_TIME_FORMATTER);
    }

    @Override
    public LocalDateTime currentLocalDateTime() {
        return currentLocalDateTime == null ? null : currentLocalDateTime;
    }
}
