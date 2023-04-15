package poc.genericresourcemanagement.test.cucumber.service;

import poc.genericresourcemanagement.application.service.TimeGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class ManualTimeGenerator implements TimeGenerator {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
            .toFormatter();;

    private LocalDateTime currentLocalDateTime;

    public void setCurrentLocalDateTime(String timeInString) {
        currentLocalDateTime = LocalDateTime.parse(timeInString, DATE_TIME_FORMATTER);
    }

    @Override
    public LocalDateTime currentLocalDateTime() {
        return currentLocalDateTime == null ? null : currentLocalDateTime;
    }
}
