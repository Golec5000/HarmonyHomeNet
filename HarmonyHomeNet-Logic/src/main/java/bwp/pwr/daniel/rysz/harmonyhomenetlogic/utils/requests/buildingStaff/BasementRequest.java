package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class BasementRequest {
    private BigDecimal area;
    private int basementNumber;

}
