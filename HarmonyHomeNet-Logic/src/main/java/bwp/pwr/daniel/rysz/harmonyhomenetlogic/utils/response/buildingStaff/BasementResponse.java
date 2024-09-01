package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BasementResponse(
        UUID id,
        int basementNumber,
        BigDecimal area
) {
}
