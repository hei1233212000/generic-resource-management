package poc.genericresourcemanagement.interfaces.model;

import java.util.List;

public record ErrorResponseDto(
        List<String> errorMessages
) {
}
