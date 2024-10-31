package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Category;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProblemReportResponse(
        Long id,
        String note,
        ReportStatus reportStatus,
        Category category,
        String userName,
        String apartmentAddress,
        LocalDateTime endDate
) {
}
