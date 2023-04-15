package poc.genericresourcemanagement.application.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

public interface TimeGenerator {
    LocalDateTime currentLocalDateTime();
}
