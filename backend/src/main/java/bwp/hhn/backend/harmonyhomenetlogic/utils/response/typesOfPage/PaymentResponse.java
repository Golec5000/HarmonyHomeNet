package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentResponse(

        UUID paymentId,
        PaymentStatus paymentStatus,
        LocalDateTime paymentDate,
        LocalDateTime paymentTime,
        BigDecimal paymentAmount,
        LocalDateTime createdAt,
        String description
) {
}
