package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record PollResponse(
        UUID id,
        String pollName,
        String content,
        byte[] uploadData,
        Instant createdAt,
        Instant endDate,
        BigDecimal summary
) {
}
