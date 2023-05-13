package poc.genericresourcemanagement.interfaces.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableDto<R> {
    @Schema(description = "Zero-based page index (0..N)")
    private int pageNumber;

    @Schema(description = "The size of the page to be returned")
    private int pageSize;

    @Schema(description = "The number of elements returned in this page")
    private long numberOfElements;

    @Schema(description = "The total number of pages")
    private int totalPages;

    @Schema(description = "The total number of elements")
    private long totalElements;

    @Schema(description = "The data to be returned")
    private List<R> data;
}
