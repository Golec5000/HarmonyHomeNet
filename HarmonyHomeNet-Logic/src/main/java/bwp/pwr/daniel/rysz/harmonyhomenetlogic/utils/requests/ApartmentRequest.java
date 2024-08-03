package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ApartmentRequest {

    private int apartmentNumber;

    private BigDecimal area;
}
