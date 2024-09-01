package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff;

import java.math.BigDecimal;

public record BasementRequest (
        BigDecimal area,
        int basementNumber
) {
}
