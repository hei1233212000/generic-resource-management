package poc.genericresourcemanagement.application.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeGenerator {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");

    public LocalDateTime currentLocalDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }
}
