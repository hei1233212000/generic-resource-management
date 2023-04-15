package poc.genericresourcemanagement.application.service.common;

import java.time.LocalDateTime;
import java.time.ZoneId;

public interface TimeGenerator {
    LocalDateTime currentLocalDateTime();
}
