package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApartmentRequest {

    @NotEmpty
    @Size(max = 50)
    private String address;

    @NotEmpty
    @Size(max = 20)
    private String city;

    @NotNull
    @Size(min = 1, max = 60)
    private String apartmentSignature;

    @NotEmpty
    @Size(max = 6)
    @Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Invalid zip code format")
    private String zipCode;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal apartmentArea;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal apartmentPercentValue;

}
