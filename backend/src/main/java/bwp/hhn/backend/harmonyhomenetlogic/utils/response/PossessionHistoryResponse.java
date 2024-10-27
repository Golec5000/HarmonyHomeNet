package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PossessionHistoryResponse(
        String userName,
        String apartmentName,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
