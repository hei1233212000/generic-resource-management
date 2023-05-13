package poc.genericresourcemanagement.application.model;

import java.util.List;

public record Pageable<R>(
        int pageNumber,
        int pageSize,
        long numberOfElements,
        long totalElements,
        List<R> data
) {
    public int totalPages() {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) pageSize);
    }
}
