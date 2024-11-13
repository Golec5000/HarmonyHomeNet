package bwp.hhn.backend.harmonyhomenetlogic.utils.response.page;

import java.util.List;

public record PageResponse<T>(
        int currentPage,
        int pageSize,
        int totalPages,
        List<T> content,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
}
