package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ApartmentResponse(
        String address,
        String city,
        String zipCode,
        BigDecimal apartmentArea,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
