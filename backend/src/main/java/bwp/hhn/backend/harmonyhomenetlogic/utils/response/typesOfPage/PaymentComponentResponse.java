package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentComponentResponse(
        Long id,
        String componentType,
        BigDecimal unitPrice,
        BigDecimal specialMultiplier,
        BigDecimal componentAmount,
        Instant createdAt,
        Instant updatedAt,
        String unit

) {
}
