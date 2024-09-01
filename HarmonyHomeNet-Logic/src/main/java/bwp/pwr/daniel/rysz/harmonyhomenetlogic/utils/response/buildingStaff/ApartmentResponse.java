package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ApartmentResponse (
        UUID id,
        int apartmentNumber,
        BigDecimal area
){
}
