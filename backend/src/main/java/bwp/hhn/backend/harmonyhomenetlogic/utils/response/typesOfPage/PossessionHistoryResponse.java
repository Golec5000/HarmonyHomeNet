package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PossessionHistoryResponse(
        String userName,
        String apartmentName,
        Instant startDate,
        Instant endDate
) {
}
