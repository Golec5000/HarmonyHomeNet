package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ApartmentResponse(
        UUID apartmentId,
        String address,
        String city,
        String zipCode,
        BigDecimal apartmentArea,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String apartmentSignature
) {
}
