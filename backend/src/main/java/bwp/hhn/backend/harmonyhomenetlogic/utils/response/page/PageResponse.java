package bwp.hhn.backend.harmonyhomenetlogic.utils.response.page;

import java.util.List;

public record PageResponse<T>(
        int currentPage,
        int pageSize,
        List<T> content,
        boolean last
) {
}
