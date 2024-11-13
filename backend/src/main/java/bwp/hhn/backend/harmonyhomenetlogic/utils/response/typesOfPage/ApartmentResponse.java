package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record ApartmentResponse(
        UUID apartmentId,
        String address,
        String city,
        String zipCode,
        BigDecimal apartmentArea,
        Instant createdAt,
        Instant updatedAt,
        String apartmentSignature
) {
}
