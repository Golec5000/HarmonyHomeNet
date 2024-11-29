package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentResponse(
        UUID paymentId,
        PaymentStatus paymentStatus,
        Instant paymentDate,
        Instant paymentTime,
        BigDecimal paymentAmount,
        Instant createdAt,
        String description,
        Boolean readyToPay,
        String apartmentSignature
) {
}
