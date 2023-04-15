package poc.genericresourcemanagement.application.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DefaultTimeGenerator implements TimeGenerator {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");

    @Override
    public LocalDateTime currentLocalDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }
}
